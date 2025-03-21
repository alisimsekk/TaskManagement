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
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.repository.TaskRepository;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static com.alisimsek.taskmanagement.task.TaskTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskConverter taskConverter;
    @Mock
    private AttachmentConverter attachmentConverter;
    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task = getTask();
    private TaskDto taskDto = getTaskDto();
    private Project project = getProject();
    private TaskCreateRequest taskCreateRequest = getTaskCreateRequest();
    private TaskUpdateRequest taskUpdateRequest = getTaskUpdateRequest();
    private TaskStateUpdateRequest taskStateUpdateRequest = getTaskStateUpdateRequest();
    private AddAttachmentRequest addAttachmentRequest = getAddAttachmentRequest();
    private Attachment attachment = getAttachment();
    private User teamMember = getUser();
    private User nonTeamMember = getNonTeamMemberUser();
    private Page<Task> taskPage = new PageImpl<>(Collections.singletonList(task));
    private Pageable pageable = mock(Pageable.class);;
    private TaskSearchRequest taskSearchRequest = getTaskSearchRequest();

    @BeforeEach
    void setUp() {

    }

    @Test
    void createTask_Success() {
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(taskRepository.findByTitleIgnoreCaseAndProjectAndEntityStatus(anyString(), any(Project.class), any(EntityStatus.class)))
                .thenReturn(Optional.empty());
        when(taskConverter.convertToEntity(any(TaskCreateRequest.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(any(Task.class))).thenReturn(taskDto);
        when(attachmentConverter.convertToEntity(any())).thenReturn(attachment);

        TaskDto result = taskService.createTask(taskCreateRequest);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void createTask_WhenTaskAlreadyExists_ThrowsException() {
        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(taskRepository.findByTitleIgnoreCaseAndProjectAndEntityStatus(anyString(), any(Project.class), any(EntityStatus.class)))
                .thenReturn(Optional.of(task));

        assertThrows(EntityAlreadyExistsException.class, () -> taskService.createTask(taskCreateRequest));
    }

    @Test
    void createTask_WithoutAttachments_Success() {
        TaskCreateRequest requestWithoutAttachments = new TaskCreateRequest();
        requestWithoutAttachments.setTitle("Test Task");
        requestWithoutAttachments.setDescription("Description");
        requestWithoutAttachments.setProjectGuid("project-guid");

        when(projectRepository.getByGuid(anyString())).thenReturn(project);
        when(taskRepository.findByTitleIgnoreCaseAndProjectAndEntityStatus(anyString(), any(Project.class), any(EntityStatus.class)))
                .thenReturn(Optional.empty());
        when(taskConverter.convertToEntity(any(TaskCreateRequest.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.createTask(requestWithoutAttachments);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void getTaskByGuid_Success() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.getTaskByGuid(TASK_GUID);

        assertNotNull(result);
        assertEquals(taskDto, result);
        verify(taskRepository).getByGuid(TASK_GUID);
        verify(taskConverter).convert(task);
    }

    @Test
    void getAllTasks_Success() {
        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        when(taskConverter.convert(any(Task.class))).thenReturn(taskDto);

        Page<TaskDto> result = taskService.getAllTasks(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(pageable);
    }

    @Test
    void searchTask_Success() {
        when(taskRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(taskPage);
        when(taskConverter.convert(any(Task.class))).thenReturn(taskDto);

        Page<TaskDto> result = taskService.searchTask(taskSearchRequest, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(any(BooleanBuilder.class), any(Pageable.class));
        verify(taskConverter).convert(any(Task.class));
    }

    @Test
    void updateTask_Success() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskRepository.findByTitleIgnoreCaseAndPriorityAndProjectAndEntityStatus(
                anyString(), any(TaskPriority.class), any(Project.class), any(EntityStatus.class)
        )).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);
        when(attachmentConverter.convertToEntity(any())).thenReturn(attachment);

        TaskDto result = taskService.updateTask(TASK_GUID, taskUpdateRequest);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void updateTask_WhenTaskWithSameTitleExists_ThrowsException() {
        Task existingTask = new Task();
        existingTask.setGuid("other-guid");

        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskRepository.findByTitleIgnoreCaseAndPriorityAndProjectAndEntityStatus(
                anyString(), any(TaskPriority.class), any(Project.class), any(EntityStatus.class)
        )).thenReturn(Optional.of(existingTask));

        assertThrows(EntityAlreadyExistsException.class, () -> taskService.updateTask(TASK_GUID, taskUpdateRequest));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_WithSameGuid_Success() {
        Task taskWithSameGuid = new Task();
        taskWithSameGuid.setGuid(TASK_GUID);

        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskRepository.findByTitleIgnoreCaseAndPriorityAndProjectAndEntityStatus(
                anyString(), any(TaskPriority.class), any(Project.class), any(EntityStatus.class)
        )).thenReturn(Optional.of(taskWithSameGuid));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);
        when(attachmentConverter.convertToEntity(any())).thenReturn(attachment);

        TaskDto result = taskService.updateTask(TASK_GUID, taskUpdateRequest);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void updateTask_WithoutAttachments_Success() {
        TaskUpdateRequest requestWithoutAttachments = new TaskUpdateRequest();
        requestWithoutAttachments.setTitle("Updated Task");
        requestWithoutAttachments.setDescription("Updated Description");
        requestWithoutAttachments.setUserStoryDescription("Updated User Story");
        requestWithoutAttachments.setAcceptanceCriteria("Updated Criteria");
        requestWithoutAttachments.setPriority(TaskPriority.HIGH);

        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskRepository.findByTitleIgnoreCaseAndPriorityAndProjectAndEntityStatus(
                anyString(), any(TaskPriority.class), any(Project.class), any(EntityStatus.class)
        )).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(TASK_GUID, requestWithoutAttachments);

        assertNotNull(result);
        assertEquals(taskDto, result);
        verify(attachmentConverter, never()).convertToEntity(any());
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(TASK_GUID);

        verify(taskRepository).getByGuid(TASK_GUID);
        verify(taskRepository).delete(task);
    }

    @Test
    void activateTask_Success() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.activateTask(TASK_GUID);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void updateTaskState_Success() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.updateTaskState(TASK_GUID, taskStateUpdateRequest);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void addAttachmentsToTask_Success() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(attachmentConverter.convertToEntity(any())).thenReturn(attachment);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.addAttachmentsToTask(TASK_GUID, addAttachmentRequest);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void removeAttachmentFromTask_Success() {
        String attachmentGuid = "attachment-guid";
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(attachmentRepository.getByGuid(attachmentGuid)).thenReturn(attachment);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.removeAttachmentFromTask(TASK_GUID, attachmentGuid);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void assignTaskToTeamMember_Success() {
        String teamMemberGuid = "team-member-guid";
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(userRepository.getByGuid(teamMemberGuid)).thenReturn(teamMember);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskConverter.convert(task)).thenReturn(taskDto);

        TaskDto result = taskService.assignTaskToTeamMember(TASK_GUID, teamMemberGuid);

        assertNotNull(result);
        assertEquals(taskDto, result);
    }

    @Test
    void assignTaskToTeamMember_WithNonTeamMember_ThrowsException() {
        String nonTeamMemberGuid = "non-team-member-guid";
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(task);
        when(userRepository.getByGuid(nonTeamMemberGuid)).thenReturn(nonTeamMember);

        assertThrows(InvalidUserTypeException.class, () -> taskService.assignTaskToTeamMember(TASK_GUID, nonTeamMemberGuid));
        verify(taskRepository, never()).save(any(Task.class));
    }
}
