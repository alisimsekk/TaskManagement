package com.alisimsek.taskmanagement.attachment.service;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentUpdateRequest;
import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentsCreateRequest;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.AttachmentDto;
import com.alisimsek.taskmanagement.attachment.converter.AttachmentConverter;
import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.attachment.repository.AttachmentRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.task.repository.TaskRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);
    private final AttachmentRepository attachmentRepository;
    private final AttachmentConverter attachmentConverter;
    private final TaskRepository taskRepository;

    public List<AttachmentDto> createBulkAttachments(AttachmentsCreateRequest request) {
        return request.getAttachments().stream()
                .map(attachmentCreateDto -> createAttachment(request.getTaskGuid(), attachmentCreateDto))
                .collect(Collectors.toList());
    }

    public AttachmentDto createAttachment(String taskGuid, AttachmentCreateDto request) {
        Task taskFromDb = taskRepository.getByGuid(taskGuid);
        Attachment newAttachment = attachmentConverter.convertToEntity(request);
        newAttachment.setTask(taskFromDb);
        return attachmentConverter.convert(attachmentRepository.save(newAttachment));
    }

    public AttachmentDto getAttachmentByGuid(String guid) {
        return attachmentConverter.convert(attachmentRepository.getByGuid(guid));
    }

    public Page<AttachmentDto> getAllAttachments(Pageable pageable) {
        Page<Attachment> attachmentsPage = attachmentRepository.findAll(pageable);
        return attachmentsPage.map(attachmentConverter::convert);
    }

    public Page<AttachmentDto> searchAttachment(String originalFilename, String taskTitle, EntityStatus entityStatus, Pageable pageable) {
        BooleanBuilder builder = AttachmentQueryBuilder.createQuery(originalFilename, taskTitle, entityStatus);
        Page<Attachment> attachmentsPage = attachmentRepository.findAll(builder, pageable);
        return attachmentsPage.map(attachmentConverter::convert);
    }

    public AttachmentDto updateAttachment(String guid, AttachmentUpdateRequest request) {
        Optional<Attachment> duplicatedAttachment = attachmentRepository.getByoriginalFilenameAndEntityStatus(request.getOriginalFilename(), EntityStatus.ACTIVE);
        if (duplicatedAttachment.isPresent() && !guid.equals(duplicatedAttachment.get().getGuid())) {
            throw new EntityAlreadyExistsException();
        }
        Attachment attachmentFromDb = attachmentRepository.getByGuid(guid);
        attachmentFromDb.setOriginalFilename(request.getOriginalFilename());
        Attachment updatedAttachment = attachmentRepository.save(attachmentFromDb);
        log.info("Attachment with guid {} updated.", guid);
        return attachmentConverter.convert(updatedAttachment);
    }

    public void deleteAttachment(String guid) {
        Attachment attachmentFromDb = attachmentRepository.getByGuid(guid);
        attachmentRepository.delete(attachmentFromDb);
        log.info("Attachment with guid {} was deleted.", guid);
    }

    public AttachmentDto activateAttachment(String guid) {
        Attachment attachmentFromDb = attachmentRepository.getByGuid(guid);
        attachmentRepository.activate(attachmentFromDb);
        log.info("Attachment with guid {} was activated.", guid);
        return attachmentConverter.convert(attachmentFromDb);
    }
}
