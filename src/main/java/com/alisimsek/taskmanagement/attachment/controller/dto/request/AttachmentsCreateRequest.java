package com.alisimsek.taskmanagement.attachment.controller.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AttachmentsCreateRequest {
    @NotEmpty
    public List<AttachmentCreateDto> attachments;
    private String taskGuid;
}
