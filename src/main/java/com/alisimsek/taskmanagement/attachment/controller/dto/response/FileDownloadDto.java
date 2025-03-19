package com.alisimsek.taskmanagement.attachment.controller.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@Builder
public class FileDownloadDto {
    Resource resource;
    String contentType;
    String headerValue;
}
