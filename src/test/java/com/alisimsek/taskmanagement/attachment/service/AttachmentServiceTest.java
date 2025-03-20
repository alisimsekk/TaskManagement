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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.alisimsek.taskmanagement.attachment.AttachmentTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentConverter attachmentConverter;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    String guid = "attachment-guid-1";
    String taskGuid = "task-guid-1";

    @Test
    void createBulkAttachments_ShouldReturnListOfAttachmentDto() {
        AttachmentsCreateRequest request = getAttachmentCreateRequest(taskGuid, getAttachmentCreateDto());

        Task task = getTask(taskGuid);
        Attachment attachment = getAttachment(task);
        AttachmentDto attachmentDto = getAttachmentDto(taskGuid);

        when(taskRepository.getByGuid(taskGuid)).thenReturn(task);
        when(attachmentConverter.convertToEntity(any(AttachmentCreateDto.class))).thenReturn(attachment);
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(attachmentConverter.convert(any(Attachment.class))).thenReturn(attachmentDto);

        List<AttachmentDto> result = attachmentService.createBulkAttachments(request);

        assertEquals(1, result.size());
    }

    @Test
    void createAttachment_ShouldReturnAttachmentDto() {
        AttachmentCreateDto createDto = getAttachmentCreateDto();

        Task task = getTask(taskGuid);
        Attachment attachment = getAttachment(task);
        AttachmentDto attachmentDto = getAttachmentDto(taskGuid);

        when(taskRepository.getByGuid(taskGuid)).thenReturn(task);
        when(attachmentConverter.convertToEntity(createDto)).thenReturn(attachment);
        when(attachmentRepository.save(any())).thenReturn(attachment);
        when(attachmentConverter.convert(attachment)).thenReturn(attachmentDto);

        AttachmentDto result = attachmentService.createAttachment(taskGuid, createDto);

        assertNotNull(result);
        assertEquals(attachmentDto, result);
    }

    @Test
    void getAttachmentByGuid_ShouldReturnAttachmentDto() {
        Attachment attachment = getAttachment(getTask(taskGuid));
        AttachmentDto attachmentDto = getAttachmentDto(taskGuid);

        when(attachmentRepository.getByGuid(guid)).thenReturn(attachment);
        when(attachmentConverter.convert(attachment)).thenReturn(attachmentDto);

        AttachmentDto result = attachmentService.getAttachmentByGuid(guid);

        assertNotNull(result);
        assertEquals(taskGuid, result.getTaskGuid());
        assertEquals(attachmentDto, result);
    }

    @Test
    void getAllAttachments_ShouldReturnPageOfAttachmentDto() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Attachment> attachments = Collections.singletonList(getAttachment(getTask(taskGuid)));
        Page<Attachment> attachmentPage = new PageImpl<>(attachments, pageable, attachments.size());

        AttachmentDto attachmentDto = getAttachmentDto(taskGuid);

        when(attachmentRepository.findAll(pageable)).thenReturn(attachmentPage);
        when(attachmentConverter.convert(any(Attachment.class))).thenReturn(attachmentDto);

        Page<AttachmentDto> result = attachmentService.getAllAttachments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchAttachment_ShouldReturnPageOfAttachmentDto() {
        List<Attachment> attachments = Collections.singletonList(getAttachment(getTask(taskGuid)));
        Page<Attachment> attachmentPage = new PageImpl<>(attachments);
        Pageable pageable = PageRequest.of(0, 10);

        AttachmentDto attachmentDto = getAttachmentDto(taskGuid);
        when(attachmentRepository.findAll(any(BooleanBuilder.class), eq(pageable))).thenReturn(attachmentPage);
        when(attachmentConverter.convert(any(Attachment.class))).thenReturn(attachmentDto);

        Page<AttachmentDto> result = attachmentService.searchAttachment(ORIGINAL_FILE_NAME, TASK_TITLE, EntityStatus.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(attachments.getFirst().getTask().getGuid(), result.getContent().getFirst().getTaskGuid());
    }

    @Test
    void updateAttachment_ShouldThrowEntityAlreadyExistsException_WhenDuplicateExists() {
        AttachmentUpdateRequest updateRequest = getAttachmentUpdateRequest();
        Attachment duplicateAttachment = getAttachment(getTask(taskGuid));

        when(attachmentRepository.getByoriginalFilenameAndEntityStatus(updateRequest.getOriginalFilename(), EntityStatus.ACTIVE))
                .thenReturn(Optional.of(duplicateAttachment));

        assertThrows(EntityAlreadyExistsException.class, () ->
                attachmentService.updateAttachment(guid, updateRequest));

        verify(attachmentRepository).getByoriginalFilenameAndEntityStatus(updateRequest.getOriginalFilename(), EntityStatus.ACTIVE);
        verify(attachmentRepository, never()).getByGuid(anyString());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    void updateAttachment_ShouldReturnUpdatedAttachmentDto_WhenSameGuid() {
        String updatedOriginalFilename = "updated-original-filename";
        AttachmentUpdateRequest updateRequest = getAttachmentUpdateRequest();
        updateRequest.setOriginalFilename(updatedOriginalFilename);
        Attachment updatedAttachment = getAttachment(getTask(taskGuid));
        updatedAttachment.setOriginalFilename(updatedOriginalFilename);

        Attachment existingAttachment = getAttachment(getTask(taskGuid));
        AttachmentDto attachmentDto = getAttachmentDto(taskGuid);
        attachmentDto.setOriginalFilename(updatedOriginalFilename);

        when(attachmentRepository.getByoriginalFilenameAndEntityStatus(updateRequest.getOriginalFilename(), EntityStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(attachmentRepository.getByGuid(guid)).thenReturn(existingAttachment);
        when(attachmentRepository.save(existingAttachment)).thenReturn(updatedAttachment);
        when(attachmentConverter.convert(any())).thenReturn(attachmentDto);

        AttachmentDto result = attachmentService.updateAttachment(guid, updateRequest);

        assertNotNull(result);
        assertEquals(updatedOriginalFilename, result.getOriginalFilename());
    }

    @Test
    void deleteAttachment_ShouldDeleteAttachment() {
        Attachment attachment = getAttachment(getTask(taskGuid));

        when(attachmentRepository.getByGuid(guid)).thenReturn(attachment);

        attachmentService.deleteAttachment(guid);

        verify(attachmentRepository).getByGuid(guid);
        verify(attachmentRepository).delete(attachment);
    }

    @Test
    void activateAttachment_ShouldActivateAndReturnAttachmentDto() {
        Attachment attachment = getAttachment(getTask(taskGuid));
        AttachmentDto attachmentDto = getAttachmentDto(guid);

        when(attachmentRepository.getByGuid(guid)).thenReturn(attachment);
        when(attachmentConverter.convert(attachment)).thenReturn(attachmentDto);

        AttachmentDto result = attachmentService.activateAttachment(guid);

        assertNotNull(result);
        verify(attachmentRepository).getByGuid(guid);
        verify(attachmentRepository).activate(attachment);
    }
}
