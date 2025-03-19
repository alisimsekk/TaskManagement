package com.alisimsek.taskmanagement.comment.service;

import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentCreateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentUpdateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.response.CommentDto;
import com.alisimsek.taskmanagement.comment.converter.CommentConverter;
import com.alisimsek.taskmanagement.comment.entity.Comment;
import com.alisimsek.taskmanagement.comment.repository.CommentRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.AccessDeniedException;
import com.alisimsek.taskmanagement.security.CurrentPrincipalProvider;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.task.repository.TaskRepository;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentConverter commentConverter;
    private final TaskRepository taskRepository;
    private final CurrentPrincipalProvider currentPrincipalProvider;

    public CommentDto createComment(CommentCreateRequest request) {
        Task taskFromDb = taskRepository.getByGuid(request.getTaskGuid());
        Comment newComment = commentConverter.convertToEntity(request);

        User user = currentPrincipalProvider.getCurrentUser();
        newComment.setAuthor(user);

        newComment.setTask(taskFromDb);
        return commentConverter.convert(commentRepository.save(newComment));
    }

    public CommentDto getCommentByGuid(String guid) {
        return commentConverter.convert(commentRepository.getByGuid(guid));
    }

    public Page<CommentDto> getAllComments(Pageable pageable) {
        Page<Comment> commentsPage = commentRepository.findAll(pageable);
        return commentsPage.map(commentConverter::convert);
    }

    public Page<CommentDto> searchComment(String taskTitle, String authorFirstName, String authorLastName, EntityStatus entityStatus, Pageable pageable) {
        BooleanBuilder builder = CommentQueryBuilder.createQuery(taskTitle, authorFirstName, authorLastName, entityStatus);
        Page<Comment> commentsPage = commentRepository.findAll(builder, pageable);
        return commentsPage.map(commentConverter::convert);
    }

    public CommentDto updateComment(String guid, CommentUpdateRequest request) {
        Comment commentFromDb = commentRepository.getByGuid(guid);
        commentFromDb.setContent(request.getContent());
        if (!commentFromDb.getAuthor().getId().equals(currentPrincipalProvider.getCurrentUser().getId())) {
            throw new AccessDeniedException();
        }
        Comment updatedComment = commentRepository.save(commentFromDb);
        log.info("Comment with guid {} was updated", updatedComment.getGuid());
        return commentConverter.convert(updatedComment);
    }

    public void deleteComment(String guid) {
        Comment commentFromDb = commentRepository.getByGuid(guid);
        commentRepository.delete(commentFromDb);
        log.info("Comment with guid {} was deleted", commentFromDb.getGuid());
    }

    public CommentDto activateComment(String guid) {
        Comment commentFromDb = commentRepository.getByGuid(guid);
        commentRepository.activate(commentFromDb);
        log.info("Comment with guid {} was activated", commentFromDb.getGuid());
        return commentConverter.convert(commentFromDb);
    }
}
