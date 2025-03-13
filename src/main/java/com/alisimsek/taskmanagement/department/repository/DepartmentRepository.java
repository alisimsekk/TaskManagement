package com.alisimsek.taskmanagement.department.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.department.entity.Department;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Long> {
}
