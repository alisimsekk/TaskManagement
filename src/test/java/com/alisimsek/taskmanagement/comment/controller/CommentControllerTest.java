package com.alisimsek.taskmanagement.comment.controller;

import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentCreateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.request.CommentUpdateRequest;
import com.alisimsek.taskmanagement.comment.controller.dto.response.CommentDto;
import com.alisimsek.taskmanagement.comment.service.CommentService;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;
import java.util.List;

import static com.alisimsek.taskmanagement.comment.CommentTestProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For handling LocalDateTime
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    public void testCreateComment() throws Exception {
        CommentCreateRequest request = getTestCommentCreateRequest();

        CommentDto responseDto = getTestCommentDto();

        when(commentService.createComment(any(CommentCreateRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post(COMMENT_BASE_API_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(COMMENT_GUID))
                .andExpect(jsonPath("$.data.taskGuid").value(TASK_GUID));
    }

    @Test
    public void testGetCommentByGuid() throws Exception {
        CommentDto responseDto = getTestCommentDto();

        when(commentService.getCommentByGuid(COMMENT_GUID)).thenReturn(responseDto);

        mockMvc.perform(get( COMMENT_BASE_API_PATH + "/{guid}", COMMENT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(COMMENT_GUID))
                .andExpect(jsonPath("$.data.taskGuid").value(TASK_GUID))
                .andExpect(jsonPath("$.data.content").value(COMMENT_CONTENT));
    }

    @Test
    public void testGetAllComments() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<CommentDto> comments = Collections.singletonList(getTestCommentDto());
        Page<CommentDto> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(commentService.getAllComments(any(Pageable.class))).thenReturn(commentPage);

        mockMvc.perform(get(COMMENT_BASE_API_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(header().string("X-Total-Pages", "1"))
                .andExpect(jsonPath("$.data[0].guid").value(COMMENT_GUID))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    public void testSearchComments() throws Exception {
        String taskTitle = "Test";
        String authorFirstName = "John";
        String authorLastName = "Doe";
        EntityStatus status = EntityStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);

        List<CommentDto> comments = Collections.singletonList(getTestCommentDto());

        Page<CommentDto> commentPage = new PageImpl<>(comments, pageable, comments.size());

        when(commentService.searchComment(eq(taskTitle), eq(authorFirstName), eq(authorLastName), eq(status), any(Pageable.class)))
                .thenReturn(commentPage);

        mockMvc.perform(get("/api/v1/comments/search")
                        .param("taskTitle", taskTitle)
                        .param("authorFirstName", authorFirstName)
                        .param("authorLastName", authorLastName)
                        .param("entityStatus", status.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(header().string("X-Total-Pages", "1"))
                .andExpect(jsonPath("$.data[0].guid").value(COMMENT_GUID))
                .andExpect(jsonPath("$.data[0].authorGuid").value(AUTHOR_GUID))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    public void testUpdateComment() throws Exception {
        CommentUpdateRequest request = getTestCommentUpdateRequest();
        CommentDto responseDto = getTestCommentDto();
        responseDto.setContent(UPDATED_CONTENT);

        when(commentService.updateComment(eq(COMMENT_GUID), any(CommentUpdateRequest.class))).thenReturn(responseDto);

        mockMvc.perform(put(COMMENT_BASE_API_PATH + "/{guid}", COMMENT_GUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(COMMENT_GUID))
                .andExpect(jsonPath("$.data.content").value(UPDATED_CONTENT));
    }

    @Test
    public void testDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(COMMENT_GUID);

        mockMvc.perform(delete(COMMENT_BASE_API_PATH + "/{guid}", COMMENT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void testActivateComment() throws Exception {
        CommentDto responseDto = getTestCommentDto();

        when(commentService.activateComment(COMMENT_GUID)).thenReturn(responseDto);

        mockMvc.perform(post(COMMENT_BASE_API_PATH + "/activate/{guid}", COMMENT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(COMMENT_GUID))
                .andExpect(jsonPath("$.data.content").value(COMMENT_CONTENT));
    }
}
