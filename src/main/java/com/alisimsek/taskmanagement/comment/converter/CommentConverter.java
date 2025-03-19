package com.alisimsek.taskmanagement.comment.converter;

import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentCreateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.response.CommentDto;
import com.alisimsek.taskmanagement.comment.entity.Comment;
import com.alisimsek.taskmanagement.common.BaseConverter;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter extends BaseConverter<Comment, CommentDto> {
    @Override
    public CommentDto convert(Comment source) {
        return CommentDto.builder()
                .guid(source.getGuid())
                .content(source.getContent())
                .taskGuid(source.getTask().getGuid())
                .authorGuid(source.getAuthor().getGuid())
                .entityStatus(source.getEntityStatus())
                .build();
    }

    public Comment convertToEntity(CommentCreateRequest request) {
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        return comment;
    }
}
