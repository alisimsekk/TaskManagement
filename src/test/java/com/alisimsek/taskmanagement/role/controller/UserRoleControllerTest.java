package com.alisimsek.taskmanagement.role.controller;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.role.controller.dto.request.UserRoleCreateRequest;
import com.alisimsek.taskmanagement.role.controller.dto.response.UserRoleDto;
import com.alisimsek.taskmanagement.role.service.UserRoleService;
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

import static com.alisimsek.taskmanagement.role.UserRoleTestProvider.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserRoleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private UserRoleController userRoleController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private UserRoleDto userRoleDto;
    private UserRoleCreateRequest createRequest;
    private List<UserRoleDto> userRoleDtoList;
    private Page<UserRoleDto> userRolePage;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userRoleController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
        userRoleDto = getUserRoleDto();
        createRequest = getUserRoleCreateRequest();
        userRoleDtoList = Collections.singletonList(userRoleDto);
        userRolePage = new PageImpl<>(userRoleDtoList, PageRequest.of(0, 10), 1);
    }

    @Test
    void shouldCreateUserRole() throws Exception {
        when(userRoleService.createUserRole(any(UserRoleCreateRequest.class))).thenReturn(userRoleDto);

        mockMvc.perform(post(ROLE_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(userRoleDto.getGuid())))
                .andExpect(jsonPath("$.data.name", is(userRoleDto.getName())));
    }

    @Test
    void shouldGetUserRoleByGuid() throws Exception {
        when(userRoleService.getUserRoleByGuid(ROLE_GUID)).thenReturn(userRoleDto);

        mockMvc.perform(get(ROLE_BASE_URI + "/{guid}", ROLE_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(userRoleDto.getGuid())))
                .andExpect(jsonPath("$.data.name", is(userRoleDto.getName())));
    }

    @Test
    void shouldGetAllUserRoles() throws Exception {
        when(userRoleService.getAllUserRoles(any(Pageable.class))).thenReturn(userRolePage);

        mockMvc.perform(get(ROLE_BASE_URI)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].guid", is(userRoleDto.getGuid())))
                .andExpect(jsonPath("$.data[0].name", is(userRoleDto.getName())));
    }

    @Test
    void shouldSearchUserRoles() throws Exception {
        when(userRoleService.searchUserRoles(eq(ROLE_NAME), eq(EntityStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(userRolePage);

        mockMvc.perform(get(ROLE_BASE_URI + "/search")
                        .param("name", ROLE_NAME)
                        .param("entityStatus", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].guid", is(userRoleDto.getGuid())))
                .andExpect(jsonPath("$.data[0].name", is(userRoleDto.getName())));
    }

    @Test
    void shouldUpdateUserRole() throws Exception {
        when(userRoleService.updateUserRole(eq(ROLE_GUID), any(UserRoleCreateRequest.class))).thenReturn(userRoleDto);

        mockMvc.perform(put(ROLE_BASE_URI + "/{guid}", ROLE_GUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(userRoleDto.getGuid())))
                .andExpect(jsonPath("$.data.name", is(userRoleDto.getName())))
                .andExpect(jsonPath("$.data.description", is(userRoleDto.getDescription())));
    }

    @Test
    void shouldDeleteUserRole() throws Exception {
        doNothing().when(userRoleService).deleteUserRole(ROLE_GUID);

        mockMvc.perform(delete(ROLE_BASE_URI + "/{guid}", ROLE_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void shouldActivateUserRole() throws Exception {
        when(userRoleService.activateUserRole(ROLE_GUID)).thenReturn(userRoleDto);

        mockMvc.perform(post(ROLE_BASE_URI + "/activate/{guid}", ROLE_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(userRoleDto.getGuid())))
                .andExpect(jsonPath("$.data.name", is(userRoleDto.getName())))
                .andExpect(jsonPath("$.data.description", is(userRoleDto.getDescription())));
    }

/*    @Test
    void shouldValidateUserRoleCreateRequest() throws Exception {
        UserRoleCreateRequest invalidRequest = new UserRoleCreateRequest();

        mockMvc.perform(post("/api/v1/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(userRoleService, never()).createUserRole(any(UserRoleCreateRequest.class));
    }*/

  /*  @Test
    void shouldSearchUserRolesWithDefaultParameters() throws Exception {
        when(userRoleService.searchUserRoles(eq(null), eq(EntityStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(userRolePage);

        mockMvc.perform(get("/api/v1/roles/search")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Total-Count"))
                .andExpect(header().exists("X-Total-Pages"))
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(userRoleService, times(1)).searchUserRoles(eq(null), eq(EntityStatus.ACTIVE), any(Pageable.class));
    }*/
}
