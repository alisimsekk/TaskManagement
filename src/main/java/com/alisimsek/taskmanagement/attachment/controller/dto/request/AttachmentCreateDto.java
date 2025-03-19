package com.alisimsek.taskmanagement.attachment.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AttachmentCreateDto {
    @NotBlank
    private String originalFilename;
    @NotBlank
    private String uniqueFilename;
    @NotBlank
    private String fileDownloadUri;
}
