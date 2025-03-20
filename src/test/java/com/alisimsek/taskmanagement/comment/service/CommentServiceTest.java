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
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static com.alisimsek.taskmanagement.comment.CommentTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentConverter commentConverter;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CurrentPrincipalProvider currentPrincipalProvider;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Task testTask;
    private Comment testComment;
    private CommentDto testCommentDto;
    private CommentCreateRequest testCreateRequest;
    private CommentUpdateRequest testUpdateRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
        testUser = getTestUser();
        testTask = getTestTask();
        testComment = getTestComment(testUser, testTask);
        testCommentDto = getTestCommentDto();
        testCreateRequest = getTestCommentCreateRequest();
        testUpdateRequest = getTestCommentUpdateRequest();
    }

    @Test
    void createComment_shouldCreateAndReturnComment() {
        when(taskRepository.getByGuid(TASK_GUID)).thenReturn(testTask);
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(testUser);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto result = commentService.createComment(testCreateRequest);

        assertNotNull(result);
        assertEquals(testCommentDto, result);
    }

    @Test
    void getCommentByGuid_shouldReturnComment() {
        when(commentRepository.getByGuid(COMMENT_GUID)).thenReturn(testComment);

        CommentDto result = commentService.getCommentByGuid(COMMENT_GUID);

        assertNotNull(result);
        assertEquals(testCommentDto, result);
    }

    @Test
    void getAllComments_shouldReturnAllComments() {
        List<Comment> comments = Collections.singletonList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(commentRepository.findAll(pageable)).thenReturn(commentPage);

        Page<CommentDto> result = commentService.getAllComments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCommentDto, result.getContent().getFirst());
    }

    @Test
    void searchComment_shouldReturnFilteredComments() {
        String taskTitle = "Test Task";
        String authorFirstName = "John";
        String authorLastName = "Doe";
        EntityStatus status = EntityStatus.ACTIVE;

        List<Comment> comments = Collections.singletonList(testComment);
        Page<Comment> commentPage = new PageImpl<>(comments);
        Pageable pageable = mock(Pageable.class);

        when(commentRepository.findAll(any(BooleanBuilder.class), eq(pageable))).thenReturn(commentPage);

        Page<CommentDto> result = commentService.searchComment(taskTitle, authorFirstName, authorLastName, status, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCommentDto, result.getContent().getFirst());
    }

    @Test
    void updateComment_shouldUpdateAndReturnComment_whenUserIsAuthor() {
        testCommentDto.setContent(UPDATED_CONTENT);

        when(commentRepository.getByGuid(COMMENT_GUID)).thenReturn(testComment);
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(testUser);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto result = commentService.updateComment(COMMENT_GUID, testUpdateRequest);

        assertNotNull(result);
        assertEquals(testCommentDto, result);
    }

    @Test
    void updateComment_shouldThrowAccessDeniedException_whenUserIsNotAuthor() {
        User differentUser = getTestUser();
        differentUser.setId(2L);

        when(commentRepository.getByGuid(COMMENT_GUID)).thenReturn(testComment);
        when(currentPrincipalProvider.getCurrentUser()).thenReturn(differentUser);

        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(COMMENT_GUID, testUpdateRequest));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_shouldDeleteComment() {
        when(commentRepository.getByGuid(COMMENT_GUID)).thenReturn(testComment);

        commentService.deleteComment(COMMENT_GUID);

        verify(commentRepository).delete(testComment);
    }

    @Test
    void activateComment_shouldActivateAndReturnComment() {
        when(commentRepository.getByGuid(COMMENT_GUID)).thenReturn(testComment);
        doNothing().when(commentRepository).activate(testComment);

        CommentDto result = commentService.activateComment(COMMENT_GUID);

        assertNotNull(result);
        assertEquals(testCommentDto, result);

        verify(commentRepository).activate(testComment);
    }
}
