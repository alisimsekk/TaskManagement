package com.alisimsek.taskmanagement.attachment;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentUpdateRequest;
import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentsCreateRequest;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.AttachmentDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileDownloadDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.FileUploadResponseDto;
import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.task.entity.Task;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;

public class AttachmentTestProvider {

    public static final String ORIGINAL_FILE_NAME = "test originalFile";
    public static final String UNIQUE_FILE_NAME = "test uniqueFilename";
    public static final String FILE_DOWNLOAD_URI = "test fileDownloadUri";
    public static final String ATTACHMENT_API_BASE_PATH = "/api/v1/attachments";
    public static final String TASK_TITLE = "Test Task";

    public static AttachmentsCreateRequest getAttachmentCreateRequest(String taskGuid, AttachmentCreateDto attachmentCreateDto) {
        AttachmentsCreateRequest attachmentsCreateRequest = new AttachmentsCreateRequest();
        attachmentsCreateRequest.setTaskGuid(taskGuid);
        List<AttachmentCreateDto> attachments = Collections.singletonList(attachmentCreateDto);
        attachmentsCreateRequest.setAttachments(attachments);
        return attachmentsCreateRequest;
    }

    public static AttachmentCreateDto getAttachmentCreateDto() {
        AttachmentCreateDto attachmentCreateDto = new AttachmentCreateDto();
        attachmentCreateDto.setOriginalFilename(ORIGINAL_FILE_NAME);
        attachmentCreateDto.setUniqueFilename(UNIQUE_FILE_NAME);
        attachmentCreateDto.setFileDownloadUri(FILE_DOWNLOAD_URI);
        return attachmentCreateDto;
    }

    public static AttachmentDto getAttachmentDto(String taskGuid) {
        return AttachmentDto.builder()
                .originalFilename(ORIGINAL_FILE_NAME)
                .uniqueFilename(UNIQUE_FILE_NAME)
                .fileDownloadUri(FILE_DOWNLOAD_URI)
                .taskGuid(taskGuid)
                .build();
    }

    public static AttachmentUpdateRequest getAttachmentUpdateRequest() {
        AttachmentUpdateRequest attachmentUpdateRequest = new AttachmentUpdateRequest();
        attachmentUpdateRequest.setOriginalFilename("updated-file.txt");
        return attachmentUpdateRequest;
    }

    public static MockMultipartFile getMockMultipartFile() {
        return new MockMultipartFile("file", "test-file.txt", MediaType.TEXT_PLAIN_VALUE, "test content".getBytes());
    }

    public static FileUploadResponseDto getFileUploadResponseDto() {
        return FileUploadResponseDto.builder().originalFilename("test-file.txt").build();
    }

    public static FileDownloadDto getFileDownloadDto() {
        return FileDownloadDto.builder()
                .resource(new ByteArrayResource("test content".getBytes()))
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .headerValue("attachment; filename=\"test-file.txt\"")
                .build();
    }

    public static Task getTask(String guid) {
        Task task = new Task();
        task.setGuid(guid);
        task.setTitle(TASK_TITLE);
        task.setDescription("Test Description");
        return task;
    }

    public static Attachment getAttachment(Task task) {
        Attachment attachment = new Attachment();
        attachment.setOriginalFilename(ORIGINAL_FILE_NAME);
        attachment.setUniqueFilename(UNIQUE_FILE_NAME);
        attachment.setFileDownloadUri(FILE_DOWNLOAD_URI);
        attachment.setTask(task);
        return attachment;
    }
}
