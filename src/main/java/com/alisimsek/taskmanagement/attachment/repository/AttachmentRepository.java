package com.alisimsek.taskmanagement.attachment.repository;

import com.alisimsek.taskmanagement.attachment.entity.Attachment;
import com.alisimsek.taskmanagement.common.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends BaseRepository<Attachment, Long> {
}
