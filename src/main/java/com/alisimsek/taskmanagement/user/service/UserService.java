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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserRoleRespository userRoleRespository;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    public UserDto createUser(UserCreateRequest request) {
        Optional<User> userFromDb = userRepository.findByUsername(request.getUsername());
        if (userFromDb.isPresent()) {
            throw new EntityAlreadyExistsException();
        }
        User newUser = userConverter.convertToEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        return userConverter.convert(userRepository.save(newUser));
    }

    public UserDto getUserByGuid(String guid) {
        return userConverter.convert(userRepository.getByGuid(guid));
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userConverter::convert);
    }

    public Page<UserDto> searchUsers(UserSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = UserQueryBuilder.createQuery(request);
        Page<User> usersPage = userRepository.findAll(builder, pageable);
        return usersPage.map(userConverter::convert);
    }

    public UserDto updateUser(String guid, UserUpdateRequest request) {
        User userFromDb = userRepository.getByGuid(guid);
        if (Objects.nonNull(request.getPassword())){
            userFromDb.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userFromDb.setFirstName(request.getFirstName());
        userFromDb.setLastName(request.getLastName());
        return userConverter.convert(userRepository.save(userFromDb));
    }

    public void deleteUser(String guid) {
        User userFromDb = userRepository.getByGuid(guid);
        userFromDb.disabled();
        userRepository.delete(userFromDb);
        log.info("User with guid {} deleted", guid);
    }

    public UserDto activateUser(String guid) {
        User userFromDb = userRepository.getByGuid(guid);
        userFromDb.enabled();
        userRepository.activate(userFromDb);
        log.info("User with guid {} activated", guid);
        return userConverter.convert(userFromDb);
    }

    public UserDto addUserRoleToUser(String userGuid, String userRoleGuid) {
        UserRole userRole = userRoleRespository.getByGuid(userRoleGuid);
        User user = userRepository.getByGuid(userGuid);
        user.addRole(userRole);
        return userConverter.convert(userRepository.save(user));
    }
}
