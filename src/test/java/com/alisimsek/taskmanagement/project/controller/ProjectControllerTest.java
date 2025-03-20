package com.alisimsek.taskmanagement.project.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddDepartmentToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.AddTeamMemberToProjectRequest;
import com.alisimsek.taskmanagement.project.controller.dto.request.ProjectCreateRequest;
import com.alisimsek.taskmanagement.project.controller.dto.response.ProjectDto;
import com.alisimsek.taskmanagement.project.entity.ProjectStatus;
import com.alisimsek.taskmanagement.project.service.ProjectService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.List;

import static com.alisimsek.taskmanagement.project.ProjectTestProvider.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private ObjectMapper objectMapper;
    private ProjectDto projectDto;
    private ProjectCreateRequest projectCreateRequest;
    private AddDepartmentToProjectRequest addDepartmentToProjectRequest;
    private AddTeamMemberToProjectRequest addTeamMemberToProjectRequest;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();

        projectDto = getProjectDto();
        projectCreateRequest = getProjectCreateRequest();
        addDepartmentToProjectRequest = getAddDepartmentToProjectRequest();
        addTeamMemberToProjectRequest = getAddTeamMemberToProjectRequest();
        headers = new HttpHeaders();
        headers.add("X-Total-Count", "1");
    }

    @Test
    void createProject_ShouldReturnCreatedProject() throws Exception {
        when(projectService.createProject(any(ProjectCreateRequest.class))).thenReturn(projectDto);

        mockMvc.perform(post(PROJECT_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(projectDto.getTitle()));

        verify(projectService).createProject(any(ProjectCreateRequest.class));
    }

    @Test
    void getProjectByGuid_ShouldReturnProject() throws Exception {
        when(projectService.getProjectByGuid(anyString())).thenReturn(projectDto);

        mockMvc.perform(get( PROJECT_BASE_URI+ "/{guid}", PROJECT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(projectDto.getTitle()));
    }

    @Test
    void getAllProjects_ShouldReturnAllProjects() throws Exception {
        List<ProjectDto> projects = List.of(projectDto);
        Page<ProjectDto> projectPage = new PageImpl<>(projects);

        when(projectService.getAllProjects(any(Pageable.class))).thenReturn(projectPage);

        mockMvc.perform(get(PROJECT_BASE_URI))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data[0].title").value(projectDto.getTitle()));
    }

    @Test
    void searchProjects_ShouldReturnMatchingProjects() throws Exception {
        List<ProjectDto> projects = List.of(projectDto);
        Page<ProjectDto> projectPage = new PageImpl<>(projects);

        when(projectService.searchProjects(anyString(), any(ProjectStatus.class), anyString(), any(EntityStatus.class), any(Pageable.class)))
                .thenReturn(projectPage);

        mockMvc.perform(get(PROJECT_BASE_URI + "/search")
                        .param("title", "Test Project")
                        .param("status", ProjectStatus.IN_PROGRESS.name())
                        .param("departmentName", "IT")
                        .param("entityStatus", EntityStatus.ACTIVE.name()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data[0].title").value(projectDto.getTitle()));
    }

    @Test
    void updateProject_ShouldReturnUpdatedProject() throws Exception {
        // Given
        String guid = "project-guid";
        when(projectService.updateProject(anyString(), any(ProjectCreateRequest.class))).thenReturn(projectDto);

        // When & Then
        mockMvc.perform(put(PROJECT_BASE_URI + "/{guid}", guid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(projectDto.getTitle()));

        verify(projectService).updateProject(eq(guid), any(ProjectCreateRequest.class));
    }

    @Test
    void deleteProject_ShouldReturnSuccess() throws Exception {
        doNothing().when(projectService).deleteProject(anyString());

        // When & Then
        mockMvc.perform(delete(PROJECT_BASE_URI + "/{guid}", PROJECT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(projectService).deleteProject(PROJECT_GUID);
    }

    @Test
    void activateProject_ShouldReturnActivatedProject() throws Exception {
        when(projectService.activateProject(anyString())).thenReturn(projectDto);

        mockMvc.perform(post( PROJECT_BASE_URI + "/activate/{guid}", PROJECT_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(projectDto.getTitle()));

        verify(projectService).activateProject(PROJECT_GUID);
    }

    @Test
    void addDepartmentToProject_ShouldReturnProjectWithDepartment() throws Exception {
        when(projectService.addDepartmentToProject(any(AddDepartmentToProjectRequest.class))).thenReturn(projectDto);

        mockMvc.perform(post(PROJECT_BASE_URI + "/add-department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addDepartmentToProjectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(projectDto.getTitle()));

        verify(projectService).addDepartmentToProject(any(AddDepartmentToProjectRequest.class));
    }

    @Test
    void addTeamMemberToProject_ShouldReturnProjectWithTeamMember() throws Exception {
        when(projectService.addTeamMemberToProject(any(AddTeamMemberToProjectRequest.class))).thenReturn(projectDto);

        mockMvc.perform(post(PROJECT_BASE_URI + "/add-team-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTeamMemberToProjectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid").value(projectDto.getGuid()))
                .andExpect(jsonPath("$.data.title").value(projectDto.getTitle()));
        verify(projectService).addTeamMemberToProject(any(AddTeamMemberToProjectRequest.class));
    }


}
