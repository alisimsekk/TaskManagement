package com.alisimsek.taskmanagement.task.controller.dto.request;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AddAttachmentRequest {
    @NotEmpty
    public List<AttachmentCreateDto> attachments;
}
