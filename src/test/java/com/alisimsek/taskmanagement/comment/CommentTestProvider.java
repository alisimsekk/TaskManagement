package com.alisimsek.taskmanagement.comment;

import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentCreateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentUpdateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.response.CommentDto;
import com.alisimsek.taskmanagement.comment.entity.Comment;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.user.entity.User;

public class CommentTestProvider {

    public static String COMMENT_CONTENT = "Test comment content";
    public static String TASK_GUID = "task-123";
    public static String COMMENT_GUID = "comment-123";
    public static String UPDATED_CONTENT = "Updated comment content";
    public static String AUTHOR_GUID = "author-123";
    public static String COMMENT_BASE_API_PATH = "/api/v1/comments";

    public static CommentCreateRequest getTestCommentCreateRequest() {
        CommentCreateRequest request = new CommentCreateRequest();
        request.setTaskGuid(TASK_GUID);
        request.setContent(COMMENT_CONTENT);
        return request;
    }

    public static User getTestUser() {
        User user = new User();
        user.setId(1L);
        user.setGuid(AUTHOR_GUID);
        user.setFirstName("John");
        user.setLastName("Doe");
        return user;
    }

    public static Task getTestTask() {
        Task task = new Task();
        task.setId(1L);
        task.setGuid(TASK_GUID);
        task.setTitle("Test Task");
        task.setDescription("Test Task Description");
        return task;
    }

    public static Comment getTestComment(User author, Task task) {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setGuid(COMMENT_GUID);
        comment.setContent(COMMENT_CONTENT);
        comment.setAuthor(author);
        comment.setTask(task);
        return comment;
    }

    public static CommentDto getTestCommentDto() {
        return CommentDto.builder()
                .guid(COMMENT_GUID)
                .content(COMMENT_CONTENT)
                .taskGuid(TASK_GUID)
                .authorGuid(AUTHOR_GUID)
                .entityStatus(EntityStatus.ACTIVE)
                .build();
    }

    public static CommentUpdateRequest getTestCommentUpdateRequest() {
        CommentUpdateRequest request = new CommentUpdateRequest();
        request.setContent(UPDATED_CONTENT);
        return request;
    }
}
