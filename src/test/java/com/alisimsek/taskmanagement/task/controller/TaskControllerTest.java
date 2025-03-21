package com.alisimsek.taskmanagement.task.controller;

import com.alisimsek.taskmanagement.task.controller.dto.request.*;
import com.alisimsek.taskmanagement.task.controller.dto.response.TaskDto;
import com.alisimsek.taskmanagement.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static com.alisimsek.taskmanagement.task.TaskTestProvider.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private TaskCreateRequest taskCreateRequest = getTaskCreateRequest();
    private TaskDto taskDto = getTaskDto();
    private TaskSearchRequest searchRequest = getTaskSearchRequest();
    private TaskUpdateRequest taskUpdateRequest = getTaskUpdateRequest();
    private TaskStateUpdateRequest taskStateUpdateRequest = getTaskStateUpdateRequest();
    private AddAttachmentRequest addAttachmentRequest = getAddAttachmentRequest();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void createTask_Success() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(taskDto);

        mockMvc.perform(post(TASK_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(taskDto.getTitle()));
    }

    @Test
    void getTaskByGuid_Success() throws Exception {
        when(taskService.getTaskByGuid(TASK_GUID)).thenReturn(taskDto);

        mockMvc.perform(get(TASK_BASE_URI + "/{guid}", TASK_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(taskDto.getTitle()));
    }

    @Test
    void getAllTasks_Success() throws Exception {
        List<TaskDto> tasks = Collections.singletonList(getTaskDto());
        Page<TaskDto> taskPage = new PageImpl<>(tasks);

        when(taskService.getAllTasks(any(Pageable.class))).thenReturn(taskPage);

        mockMvc.perform(get(TASK_BASE_URI))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].guid").value(tasks.getFirst().getGuid()));
    }

    @Test
    void searchTask_Success() throws Exception {
        List<TaskDto> tasks = Collections.singletonList(getTaskDto());
        Page<TaskDto> taskPage = new PageImpl<>(tasks);

        when(taskService.searchTask(any(TaskSearchRequest.class), any(Pageable.class)))
                .thenReturn(taskPage);

        mockMvc.perform(get(TASK_BASE_URI + "/search")
                        .param(TASK_TITLE, searchRequest.getTitle())
                        .param(PROJECT_GUID, searchRequest.getProjectGuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].guid").value(tasks.getFirst().getGuid()));
    }

    @Test
    void updateTask_Success() throws Exception {
        when(taskService.updateTask(eq(TASK_GUID), any(TaskUpdateRequest.class))).thenReturn(taskDto);

        mockMvc.perform(put(TASK_BASE_URI + "/{guid}", TASK_GUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(taskDto.getTitle()));
    }

    @Test
    void deleteTask_Success() throws Exception {
        mockMvc.perform(delete(TASK_BASE_URI + "/{guid}", TASK_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
        verify(taskService, times(1)).deleteTask(TASK_GUID);
    }

    @Test
    void activateTask_Success() throws Exception {
        when(taskService.activateTask(TASK_GUID)).thenReturn(taskDto);

        // When & Then
        mockMvc.perform(post(TASK_BASE_URI + "/activate/{guid}", TASK_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()));
        verify(taskService, times(1)).activateTask(TASK_GUID);
    }

    @Test
    void updateTaskState_Success() throws Exception {
        when(taskService.updateTaskState(eq(TASK_GUID), any(TaskStateUpdateRequest.class))).thenReturn(taskDto);

        mockMvc.perform(put(TASK_BASE_URI + "/{guid}", TASK_GUID + "/update-state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStateUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()));
        verify(taskService, times(1)).updateTaskState(eq(TASK_GUID), any(TaskStateUpdateRequest.class));
    }

    @Test
    void addAttachmentsToTask_Success() throws Exception {
        when(taskService.addAttachmentsToTask(eq(TASK_GUID), any(AddAttachmentRequest.class))).thenReturn(taskDto);

        mockMvc.perform(post(TASK_BASE_URI + "/{guid}", TASK_GUID + "/add-attachments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addAttachmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()));
        verify(taskService, times(1)).addAttachmentsToTask(eq(TASK_GUID), any(AddAttachmentRequest.class));
    }

    @Test
    void removeAttachmentFromTask_Success() throws Exception {
        when(taskService.removeAttachmentFromTask(TASK_GUID, ATTACHMENT_GUID)).thenReturn(taskDto);

        mockMvc.perform(post(TASK_BASE_URI + "/{guid}", TASK_GUID + "/remove-attachment/" + ATTACHMENT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()));
        verify(taskService, times(1)).removeAttachmentFromTask(TASK_GUID, ATTACHMENT_GUID);
    }

    @Test
    void assignTaskToTeamMember_Success() throws Exception {
        when(taskService.assignTaskToTeamMember(TASK_GUID, TEAM_MEMBER_GUID)).thenReturn(taskDto);

        mockMvc.perform(post(TASK_BASE_URI + "/{guid}", TASK_GUID + "/assign/" + TEAM_MEMBER_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(taskDto.getGuid()));
        verify(taskService, times(1)).assignTaskToTeamMember(TASK_GUID, TEAM_MEMBER_GUID);
    }
}
