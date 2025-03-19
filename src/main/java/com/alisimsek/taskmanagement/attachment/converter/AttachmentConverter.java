package com.alisimsek.taskmanagement.attachment.converter;

import com.alisimsek.taskmanagement.attachment.controller.dto.request.AttachmentCreateDto;
import com.alisimsek.taskmanagement.attachment.controller.dto.response.AttachmentDto;
import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.common.BaseConverter;
import com.alisimsek.taskmanagement.task.controller.dto.request.AddAttachmentRequest;
import org.springframework.stereotype.Component;

@Component
public class AttachmentConverter extends BaseConverter<Attachment, AttachmentDto> {
    @Override
    public AttachmentDto convert(Attachment source) {
        return AttachmentDto.builder()
                .guid(source.getGuid())
                .originalFilename(source.getOriginalFilename())
                .uniqueFilename(source.getUniqueFilename())
                .fileDownloadUri(source.getFileDownloadUri())
                .taskGuid(source.getTask().getGuid())
                .entityStatus(source.getEntityStatus())
                .build();
    }

    public Attachment convertToEntity(AttachmentCreateDto request) {
        Attachment attachment = new Attachment();
        attachment.setOriginalFilename(request.getOriginalFilename());
        attachment.setUniqueFilename(request.getUniqueFilename());
        attachment.setFileDownloadUri(request.getFileDownloadUri());
        return attachment;
    }

/*    public Attachment convertToEntity(AddAttachmentRequest request) {
        Attachment attachment = new Attachment();
        attachment.setOriginalFilename(request.getOriginalFilename());
        attachment.setUniqueFilename(request.getUniqueFilename());
        attachment.setFileDownloadUri(request.getFileDownloadUri());
        return attachment;
    }*/
}
