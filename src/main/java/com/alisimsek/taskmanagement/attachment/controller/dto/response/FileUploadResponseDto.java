package com.alisimsek.taskmanagement.attachment.controller.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponseDto {
    private String originalFilename;
    private String uniqueFilename;
    private String fileDownloadUri;
    private String fileType;
    private long size;
}
