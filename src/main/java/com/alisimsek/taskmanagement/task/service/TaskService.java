package com.alisimsek.taskmanagement.task.service;

import com.alisimsek.taskmanagement.attachment.converter.AttachmentConverter;
import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.attachment.repository.AttachmentRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.common.exception.InvalidUserTypeException;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.project.repository.ProjectRepository;
import com.alisimsek.taskmanagement.task.controller.dto.converter.TaskConverter;
import com.alisimsek.taskmanagement.task.controller.dto.request.*;
import com.alisimsek.taskmanagement.task.controller.dto.response.TaskDto;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import com.alisimsek.taskmanagement.task.repository.TaskRepository;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.entity.UserType;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskConverter taskConverter;
    private final AttachmentConverter attachmentConverter;
    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;

    public TaskDto createTask(TaskCreateRequest request) {
        Project project = projectRepository.getByGuid(request.getProjectGuid());

        Optional<Task> taskFromDb = taskRepository.findByTitleIgnoreCaseAndProjectAndEntityStatus(request.getTitle(), project, EntityStatus.ACTIVE);
        if (taskFromDb.isPresent()) {
            throw new EntityAlreadyExistsException();
        }

        Task newTask = taskConverter.convertToEntity(request);
        newTask.setProject(project);

        if (Objects.nonNull(request.getAttachments()) && !request.getAttachments().isEmpty()) {
            request.getAttachments().stream()
                    .map(attachmentConverter::convertToEntity)
                    .forEach(newTask::addAttachment);
        }
        return taskConverter.convert(taskRepository.save(newTask));
    }

    public TaskDto getTaskByGuid(String guid) {
        return taskConverter.convert(taskRepository.getByGuid(guid));
    }

    public Page<TaskDto> getAllTasks(Pageable pageable) {
        Page<Task> tasksPage = taskRepository.findAll(pageable);
        return tasksPage.map(taskConverter::convert);
    }

    public Page<TaskDto> searchTask(TaskSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = TaskQueryBuilder.createQuery(request);
        Page<Task> tasksPage = taskRepository.findAll(builder, pageable);
        return tasksPage.map(taskConverter::convert);
    }

    public TaskDto updateTask(String guid, TaskUpdateRequest request) {
        Task taskFromDb = taskRepository.getByGuid(guid);

        Optional<Task> duplicatedTask = taskRepository.findByTitleIgnoreCaseAndPriorityAndProjectAndEntityStatus(request.getTitle(), request.getPriority(), taskFromDb.getProject(), EntityStatus.ACTIVE);
        if (duplicatedTask.isPresent() && !guid.equals(duplicatedTask.get().getGuid())) {
            throw new EntityAlreadyExistsException();
        }

        taskFromDb.setTitle(request.getTitle());
        taskFromDb.setDescription(request.getDescription());
        taskFromDb.setUserStoryDescription(request.getUserStoryDescription());
        taskFromDb.setAcceptanceCriteria(request.getAcceptanceCriteria());
        taskFromDb.setPriority(request.getPriority());
        if (Objects.nonNull(request.getAttachments()) && !request.getAttachments().isEmpty()) {
            taskFromDb.updateAttachments(
                    request.getAttachments().stream()
                            .map(attachmentConverter::convertToEntity)
                            .collect(Collectors.toList()));
        } else {
            taskFromDb.deleteAllAttachments();
        }

        Task updatedTask = taskRepository.save(taskFromDb);
        log.info("Task with guid {} was updated", updatedTask.getGuid());
        return taskConverter.convert(updatedTask);
    }

    public void deleteTask(String guid) {
        Task taskFromDb = taskRepository.getByGuid(guid);
        taskRepository.delete(taskFromDb);
        log.info("Task with guid {} was deleted", guid);
    }

    public TaskDto activateTask(String guid) {
        Task taskFromDb = taskRepository.getByGuid(guid);
        taskRepository.activate(taskFromDb);
        log.info("Task with guid {} was activated", guid);
        return taskConverter.convert(taskFromDb);
    }

    public TaskDto updateTaskState(String guid, TaskStateUpdateRequest request) {
        Task taskFromDb = taskRepository.getByGuid(guid);
        TaskState currentTaskState = taskFromDb.getState();
        taskFromDb.updateState(request.getState(), request.getCancellationReason(), request.getBlockReason());
        Task updatedTask = taskRepository.save(taskFromDb);
        log.info("Task state updated from {} to {} for guid: {}", currentTaskState, updatedTask.getState(), updatedTask.getGuid());
        return taskConverter.convert(updatedTask);
    }

    public TaskDto addAttachmentsToTask(String guid, AddAttachmentRequest request) {
        Task taskFromDb = taskRepository.getByGuid(guid);
        request.attachments
                .forEach( attachmentCreateDto -> {
                    Attachment newAttachment = attachmentConverter.convertToEntity(attachmentCreateDto);
                    taskFromDb.addAttachment(newAttachment);

                });
        return taskConverter.convert(taskRepository.save(taskFromDb));
    }

    public TaskDto removeAttachmentFromTask(String taskGuid, String attachmentGuid) {
        Task taskFromDb = taskRepository.getByGuid(taskGuid);
        Attachment attachmentFromDb = attachmentRepository.getByGuid(attachmentGuid);
        taskFromDb.removeAttachment(attachmentFromDb);
        return taskConverter.convert(taskRepository.save(taskFromDb));
    }

    public TaskDto assignTaskToTeamMember(String taskGuid, String teamMemberGuid) {
        Task taskFromDb = taskRepository.getByGuid(taskGuid);
        User userFromDb = userRepository.getByGuid(teamMemberGuid);
        if (!UserType.TEAM_MEMBER.equals(userFromDb.getUserType())) {
            throw new InvalidUserTypeException();
        }
        taskFromDb.setAssignee(userFromDb);
        return taskConverter.convert(taskRepository.save(taskFromDb));
    }
}
