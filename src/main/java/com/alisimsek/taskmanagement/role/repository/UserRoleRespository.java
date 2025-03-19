package com.alisimsek.taskmanagement.role.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.role.entity.UserRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRespository extends BaseRepository<UserRole, Long> {
    Optional<UserRole> findByNameAndEntityStatus(String name, EntityStatus entityStatus);
}
