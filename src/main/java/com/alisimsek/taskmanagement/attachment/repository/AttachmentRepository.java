package com.alisimsek.taskmanagement.attachment.repository;

import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends BaseRepository<Attachment, Long> {
    Optional<Attachment> getByoriginalFilenameAndEntityStatus(String originalFilename, EntityStatus entityStatus);
}
