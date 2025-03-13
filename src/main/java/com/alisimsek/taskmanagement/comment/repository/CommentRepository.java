package com.alisimsek.taskmanagement.comment.repository;

import com.alisimsek.taskmanagement.comment.entity.Comment;
import com.alisimsek.taskmanagement.common.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends BaseRepository<Comment, Long> {
}
