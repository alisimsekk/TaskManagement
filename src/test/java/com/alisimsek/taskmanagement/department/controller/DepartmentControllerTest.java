package com.alisimsek.taskmanagement.department.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.department.controller.dto.request.DepartmentCreateRequest;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.alisimsek.taskmanagement.department.DepartmentTestProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    public void testCreateDepartment() throws Exception {
        DepartmentCreateRequest request = getDepartmentCreateRequest();

        DepartmentDto responseDto = getDepartmentDto();

        when(departmentService.createDepartment(any(DepartmentCreateRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post(DEPARTMENT_API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(DEPARTMENT_NAME));}

    @Test
    public void testGetDepartmentByGuid() throws Exception {
        String guid = "dept-123";
        DepartmentDto responseDto = getDepartmentDto();

        when(departmentService.getDepartmentByGuid(guid)).thenReturn(responseDto);

        mockMvc.perform(get( DEPARTMENT_API_BASE_PATH + "/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value(DEPARTMENT_NAME));
    }

    @Test
    public void testGetAllDepartments() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<DepartmentDto> departments = Collections.singletonList(getDepartmentDto());
        Page<DepartmentDto> departmentPage = new PageImpl<>(departments, pageable, departments.size());

        when(departmentService.getAllDepartments(any(Pageable.class))).thenReturn(departmentPage);

        mockMvc.perform(get(DEPARTMENT_API_BASE_PATH)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].name").value(DEPARTMENT_NAME));
    }

    @Test
    public void testSearchDepartments() throws Exception {
        EntityStatus status = EntityStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);
        List<DepartmentDto> departments = Collections.singletonList(getDepartmentDto());
        Page<DepartmentDto> departmentPage = new PageImpl<>(departments, pageable, departments.size());

        when(departmentService.searchDepartments(any(), any(), any(Pageable.class))).thenReturn(departmentPage);

        mockMvc.perform(get( DEPARTMENT_API_BASE_PATH + "/search")
                        .param("name", DEPARTMENT_NAME)
                        .param("entityStatus", status.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(header().string("X-Total-Pages", "1"))
                .andExpect(jsonPath("$.data[0].name").value(DEPARTMENT_NAME))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    public void testUpdateDepartment() throws Exception {
        String guid = "dept-123";
        DepartmentCreateRequest request = getDepartmentCreateRequest();
        DepartmentDto responseDto = getDepartmentDto();
        when(departmentService.updateDepartment(eq(guid), any(DepartmentCreateRequest.class))).thenReturn(responseDto);

        mockMvc.perform(put( DEPARTMENT_API_BASE_PATH + "/{guid}", guid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(DEPARTMENT_NAME));
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        String guid = "dept-123";

        doNothing().when(departmentService).deleteDepartment(guid);

        mockMvc.perform(delete(DEPARTMENT_API_BASE_PATH + "/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void testActivateDepartment() throws Exception {
        String guid = "dept-123";
        DepartmentDto responseDto = getDepartmentDto();

        when(departmentService.activateDepartment(guid)).thenReturn(responseDto);

        mockMvc.perform(post(DEPARTMENT_API_BASE_PATH + "/activate/{guid}", guid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.name").value(DEPARTMENT_NAME));
    }
}
