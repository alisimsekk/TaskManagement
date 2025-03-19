package com.alisimsek.taskmanagement.role.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.role.controller.dto.converter.UserRoleConverter;
import com.alisimsek.taskmanagement.role.controller.dto.request.UserRoleCreateRequest;
import com.alisimsek.taskmanagement.role.controller.dto.response.UserRoleDto;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import com.alisimsek.taskmanagement.role.repository.UserRoleRespository;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRoleService {

    public final UserRoleRespository userRoleRespository;
    public final UserRoleConverter userRoleConverter;

    public UserRoleDto createUserRole(UserRoleCreateRequest request) {
        Optional<UserRole> taskFromDb = userRoleRespository.findByNameAndEntityStatus(request.getName(), EntityStatus.ACTIVE);
        if (taskFromDb.isPresent()) {
            throw new EntityAlreadyExistsException();
        }
        UserRole newUserRole = userRoleConverter.convertToEntity(request);
        log.info("Created new user role with name: {}", newUserRole.getName());
        return userRoleConverter.convert(userRoleRespository.save(newUserRole));
    }

    public UserRoleDto getUserRoleByGuid(String guid) {
        return userRoleConverter.convert(userRoleRespository.getByGuid(guid));
    }

    public Page<UserRoleDto> getAllUserRoles(Pageable pageable) {
        Page<UserRole> userRolesPage = userRoleRespository.findAll(pageable);
        return userRolesPage.map(userRoleConverter::convert);
    }

    public Page<UserRoleDto> searchUserRoles(String name, EntityStatus entityStatus, Pageable pageable) {
        BooleanBuilder builder = UserRoleQueryBuilder.createQuery(name, entityStatus);
        Page<UserRole> userRolesPage = userRoleRespository.findAll(builder, pageable);
        return userRolesPage.map(userRoleConverter::convert);
    }

    public UserRoleDto updateUserRole(String guid, UserRoleCreateRequest request) {
        Optional<UserRole> duplicatedUserRole = userRoleRespository.findByNameAndEntityStatus(request.getName(), EntityStatus.ACTIVE);
        if (duplicatedUserRole.isPresent() && !duplicatedUserRole.get().getGuid().equals(guid)) {
            throw new EntityAlreadyExistsException();
        }
        UserRole userRoleFromDb = userRoleRespository.getByGuid(guid);
        userRoleFromDb.setName(request.getName());
        userRoleFromDb.setDescription(request.getDescription());
        userRoleFromDb.setUserPermissions(request.getUserPermissions());
        UserRole updatedUserRole = userRoleRespository.save(userRoleFromDb);
        log.info("Updating user role with name: {}", userRoleFromDb.getName());
        return userRoleConverter.convert(updatedUserRole);
    }

    public void deleteUserRole(String guid) {
        UserRole userRoleFromDb = userRoleRespository.getByGuid(guid);
        userRoleRespository.delete(userRoleFromDb);
        log.info("Deleted user role with name: {}", userRoleFromDb.getName());
    }

    public UserRoleDto activateUserRole(String guid) {
        UserRole userRoleFromDb = userRoleRespository.getByGuid(guid);
        userRoleRespository.activate(userRoleFromDb);
        log.info("Activated user role with name: {}", userRoleFromDb.getName());
        return userRoleConverter.convert(userRoleRespository.save(userRoleFromDb));
    }
}
