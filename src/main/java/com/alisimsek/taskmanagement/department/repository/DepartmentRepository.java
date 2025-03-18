package com.alisimsek.taskmanagement.department.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.department.entity.Department;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Long> {
    Optional<Department> findByNameAndEntityStatus(String name, EntityStatus entityStatus);

    Optional<Department> findByNameIgnoreCaseAndEntityStatus(String name, EntityStatus entityStatus);
}
