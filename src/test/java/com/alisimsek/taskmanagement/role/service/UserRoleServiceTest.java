package com.alisimsek.taskmanagement.role.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.role.controller.dto.converter.UserRoleConverter;
import com.alisimsek.taskmanagement.role.controller.dto.request.UserRoleCreateRequest;
import com.alisimsek.taskmanagement.role.controller.dto.response.UserRoleDto;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import com.alisimsek.taskmanagement.role.repository.UserRoleRespository;
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
import java.util.Optional;

import static com.alisimsek.taskmanagement.role.UserRoleTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private UserRoleRespository userRoleRespository;

    @Spy
    private UserRoleConverter userRoleConverter;

    @InjectMocks
    private UserRoleService userRoleService;

    private UserRole userRole;
    private UserRoleDto userRoleDto;
    private UserRoleCreateRequest createRequest;
    private Page<UserRole> userRolePage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userRole = getUserRole();
        userRoleDto = getUserRoleDto();
        createRequest = getUserRoleCreateRequest();
        pageable = PageRequest.of(0, 10);
        userRolePage = new PageImpl<>(Collections.singletonList(userRole), pageable, 1);
    }

    @Test
    void createUserRole_Success() {
        when(userRoleRespository.findByNameAndEntityStatus(createRequest.getName(), EntityStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(userRoleRespository.save(any())).thenReturn(userRole);

        UserRoleDto result = userRoleService.createUserRole(createRequest);

        assertNotNull(result);
        assertEquals(createRequest.getName(), result.getName());
    }

    @Test
    void createUserRole_WithExistingName_ThrowsException() {
        when(userRoleRespository.findByNameAndEntityStatus(createRequest.getName(), EntityStatus.ACTIVE))
                .thenReturn(Optional.of(userRole));

        assertThrows(EntityAlreadyExistsException.class, () ->
                userRoleService.createUserRole(createRequest)
        );
        verify(userRoleRespository, never()).save(any());
    }

    @Test
    void getUserRoleByGuid_Success() {
        when(userRoleRespository.getByGuid(ROLE_GUID)).thenReturn(userRole);

        UserRoleDto result = userRoleService.getUserRoleByGuid(ROLE_GUID);

        assertNotNull(result);
        assertEquals(userRole.getName(), result.getName());
    }

    @Test
    void getAllUserRoles_Success() {
        when(userRoleRespository.findAll(pageable)).thenReturn(userRolePage);

        Page<UserRoleDto> result = userRoleService.getAllUserRoles(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userRole.getName(), result.getContent().getFirst().getName());
    }

    @Test
    void searchUserRoles_Success() {
        when(userRoleRespository.findAll(any(BooleanBuilder.class), any(Pageable.class))).thenReturn(userRolePage);

        Page<UserRoleDto> result = userRoleService.searchUserRoles(ROLE_NAME, EntityStatus.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(userRole.getName(), result.getContent().getFirst().getName());
    }

    @Test
    void updateUserRole_Success() {
        when(userRoleRespository.findByNameAndEntityStatus(createRequest.getName(), EntityStatus.ACTIVE))
                .thenReturn(Optional.of(userRole));
        when(userRoleRespository.getByGuid(ROLE_GUID)).thenReturn(userRole);
        when(userRoleRespository.save(userRole)).thenReturn(userRole);

        UserRoleDto result = userRoleService.updateUserRole(ROLE_GUID, createRequest);

        assertNotNull(result);
        assertEquals(userRole.getName(), result.getName());
   }

    @Test
    void updateUserRole_WithDuplicateName_ThrowsException() {
        UserRole anotherRole = getUserRole();
        anotherRole.setGuid("another-guid");

        when(userRoleRespository.findByNameAndEntityStatus(createRequest.getName(), EntityStatus.ACTIVE))
                .thenReturn(Optional.of(anotherRole));

        assertThrows(EntityAlreadyExistsException.class, () ->
                userRoleService.updateUserRole(ROLE_GUID, createRequest)
        );
        verify(userRoleRespository, never()).save(any());
    }

    @Test
    void deleteUserRole_Success() {
        when(userRoleRespository.getByGuid(ROLE_GUID)).thenReturn(userRole);

        userRoleService.deleteUserRole(ROLE_GUID);

        verify(userRoleRespository).getByGuid(ROLE_GUID);
        verify(userRoleRespository).delete(userRole);
    }

    @Test
    void activateUserRole_Success() {
        when(userRoleRespository.getByGuid(ROLE_GUID)).thenReturn(userRole);
        when(userRoleRespository.save(userRole)).thenReturn(userRole);

        UserRoleDto result = userRoleService.activateUserRole(ROLE_GUID);

        assertNotNull(result);
        verify(userRoleRespository).activate(userRole);
    }

    @Test
    void updateUserRole_WithDifferentName_Success() {
        UserRoleCreateRequest newRequest = getUserRoleCreateRequest();
        newRequest.setName("New Role Name");

        when(userRoleRespository.findByNameAndEntityStatus(newRequest.getName(), EntityStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(userRoleRespository.getByGuid(ROLE_GUID)).thenReturn(userRole);
        when(userRoleRespository.save(any(UserRole.class))).thenReturn(userRole);

        UserRoleDto result = userRoleService.updateUserRole(ROLE_GUID, newRequest);

        assertNotNull(result);
    }
}
