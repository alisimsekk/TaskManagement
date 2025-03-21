package com.alisimsek.taskmanagement.user.service;

import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import com.alisimsek.taskmanagement.role.repository.UserRoleRespository;
import com.alisimsek.taskmanagement.user.controller.dto.converter.UserConverter;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserCreateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserSearchRequest;
import com.alisimsek.taskmanagement.user.controller.dto.request.UserUpdateRequest;
import com.alisimsek.taskmanagement.user.controller.dto.response.UserDto;
import com.alisimsek.taskmanagement.user.entity.User;
import com.alisimsek.taskmanagement.user.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static com.alisimsek.taskmanagement.user.UserTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRespository userRoleRespository;

    @Spy
    private UserConverter userConverter;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCreateRequest createRequest;
    private UserUpdateRequest updateRequest;
    private UserRole testUserRole;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        encodedPassword = "encodedPassword123";
        testUser = getTestUser();
        createRequest = getTestUserCreateRequest();
        updateRequest = getTestUserUpdateRequest();
        testUserRole = getTestUserRole();
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByUsername(createRequest.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createRequest.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.createUser(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getUsername(), result.getUsername());
    }

    @Test
    void createUser_ThrowsEntityAlreadyExistsException_WhenUserExists() {
        when(userRepository.findByUsername(createRequest.getUsername())).thenReturn(Optional.of(testUser));

        assertThrows(EntityAlreadyExistsException.class, () -> userService.createUser(createRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserByGuid_Success() {
        when(userRepository.getByGuid(USER_GUID)).thenReturn(testUser);

        UserDto result = userService.getUserByGuid(USER_GUID);

        assertNotNull(result);
        assertEquals(testUser.getGuid(), result.getGuid());
    }

    @Test
    void getAllUsers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser.getGuid(), result.getContent().getFirst().getGuid());
    }

    @Test
    void searchUsers_Success() {
        UserSearchRequest searchRequest = new UserSearchRequest();
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser));

        when(userRepository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(userPage);

        Page<UserDto> result = userService.searchUsers(searchRequest, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser.getGuid(), result.getContent().getFirst().getGuid());

    }

    @Test
    void updateUser_WithPassword_Success() {
        when(userRepository.getByGuid(USER_GUID)).thenReturn(testUser);
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.updateUser(USER_GUID, updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getFirstName(), result.getFirstName());
        assertEquals(updateRequest.getLastName(), result.getLastName());
        assertEquals(testUser.getGuid(), result.getGuid());
    }

/*    @Test
    void updateUser_WithoutPassword_Success() {
        updateRequest.setPassword(null);

        when(userRepository.getByGuid(USER_GUID)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userConverter.convert(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.updateUser(USER_GUID, updateRequest);

        // Assert
        verify(userRepository).getByGuid(USER_GUID);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals("password123", capturedUser.getPassword()); // Original password remains
        assertEquals(updateRequest.getFirstName(), capturedUser.getFirstName());
        assertEquals(updateRequest.getLastName(), capturedUser.getLastName());
        assertEquals(testUserDto, result);
    }*/

    @Test
    void deleteUser_Success() {
        when(userRepository.getByGuid(USER_GUID)).thenReturn(testUser);

        userService.deleteUser(USER_GUID);

        verify(userRepository).delete(testUser);
 }

    @Test
    void activateUser_Success() {
        when(userRepository.getByGuid(USER_GUID)).thenReturn(testUser);

        UserDto result = userService.activateUser(USER_GUID);

        assertNotNull(result);
        assertEquals(testUser.getGuid(), result.getGuid());
    }

    @Test
    void addUserRoleToUser_Success() {
        when(userRoleRespository.getByGuid(USER_ROLE_GUID)).thenReturn(testUserRole);
        when(userRepository.getByGuid(USER_GUID)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDto result = userService.addUserRoleToUser(USER_GUID, USER_ROLE_GUID);

        assertNotNull(result);
        assertEquals(testUser.getGuid(), result.getGuid());
        assertTrue(result.getUserPermissions().containsAll(testUserRole.getUserPermissions()));
    }
}
