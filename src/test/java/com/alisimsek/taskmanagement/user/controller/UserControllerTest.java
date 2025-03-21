package com.alisimsek.taskmanagement.user.controller;

import com.alisimsek.taskmanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import com.alisimsek.taskmanagement.user.service.UserService;
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

import java.util.Collections;

import static com.alisimsek.taskmanagement.user.UserTestProvider.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDto userDto = getTestUserDto();
    private UserCreateRequest createRequest = getTestUserCreateRequest();
    private UserUpdateRequest updateRequest = getTestUserUpdateRequest();
    private Page<UserDto> userDtoPage = createTestUserDtoPage();

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(userDto);

        mockMvc.perform(post(USER_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(USER_GUID)))
                .andExpect(jsonPath("$.data.username", is(userDto.getUsername())))
                .andExpect(jsonPath("$.data.firstName", is(userDto.getFirstName())))
                .andExpect(jsonPath("$.data.lastName", is(userDto.getLastName())));

    }

    @Test
    void getUserByGuid_Success() throws Exception {
        when(userService.getUserByGuid(USER_GUID)).thenReturn(userDto);

        mockMvc.perform(get(USER_BASE_URI + "/{guid}", USER_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(USER_GUID)))
                .andExpect(jsonPath("$.data.username", is(userDto.getUsername())));

    }

    @Test
    void getAllUsers_Success() throws Exception {
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userDtoPage);

        mockMvc.perform(get(USER_BASE_URI)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.data[0].guid", is(USER_GUID)));

    }

    @Test
    void searchUsers_Success() throws Exception {
        when(userService.searchUsers(any(UserSearchRequest.class), any(Pageable.class))).thenReturn(userDtoPage);

        mockMvc.perform(get(USER_BASE_URI + "/search")
                        .param("username", "test@example.com")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andExpect(jsonPath("$.data[0].guid", is(USER_GUID)));

        verify(userService).searchUsers(any(UserSearchRequest.class), any(Pageable.class));
    }

    @Test
    void updateUser_Success() throws Exception {
        userDto.setFirstName(updateRequest.getFirstName());
        when(userService.updateUser(eq(USER_GUID), any(UserUpdateRequest.class))).thenReturn(userDto);

        mockMvc.perform(put(USER_BASE_URI + "/{guid}", USER_GUID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(USER_GUID)))
                .andExpect(jsonPath("$.data.firstName", is(userDto.getFirstName())));

        verify(userService).updateUser(eq(USER_GUID), any(UserUpdateRequest.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete(USER_BASE_URI + "/{guid}", USER_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService).deleteUser(USER_GUID);
    }

    @Test
    void activateUser_Success() throws Exception {
        when(userService.activateUser(USER_GUID)).thenReturn(userDto);

        mockMvc.perform(post(USER_BASE_URI + "/activate/{guid}", USER_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(USER_GUID)));

        verify(userService).activateUser(USER_GUID);
    }

    @Test
    void addUserRoleToUser_Success() throws Exception {
        when(userService.addUserRoleToUser(USER_GUID, USER_ROLE_GUID)).thenReturn(userDto);

        mockMvc.perform(post(USER_BASE_URI + "/{userGuid}/add-role/{userRoleGuid}", USER_GUID, USER_ROLE_GUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.guid", is(USER_GUID)));

        verify(userService).addUserRoleToUser(USER_GUID, USER_ROLE_GUID);
    }

    private Page<UserDto> createTestUserDtoPage() {
        return new PageImpl<>(Collections.singletonList(userDto), PageRequest.of(0, 10), 1);
    }
}
