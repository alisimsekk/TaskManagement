package com.alisimsek.taskmanagement.attachment.controller.dto.response;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttachmentDto {
    private String guid;
    private String uniqueFilename;
    private String originalFilename;
    private String fileDownloadUri;
    private String taskGuid;
    private EntityStatus entityStatus;
}
