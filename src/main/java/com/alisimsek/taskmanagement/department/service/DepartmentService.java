package com.alisimsek.taskmanagement.department.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.department.controller.dto.converter.DepartmentConverter;
import com.alisimsek.taskmanagement.department.controller.dto.request.DepartmentCreateRequest;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.entity.Department;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.department.repository.DepartmentRepository;
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
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentConverter departmentConverter;

    public DepartmentDto createDepartment(DepartmentCreateRequest request) {
        Optional<Department> departmentFromDb = departmentRepository.findByNameAndEntityStatus(request.getName(), EntityStatus.ACTIVE);
        if (departmentFromDb.isPresent()) {
            throw new EntityAlreadyExistsException();
        }
        Department newDepartment = departmentRepository.save(Department.create(request.getName()));
        log.info("Created department -> {}", newDepartment.getName());
        return departmentConverter.convert(newDepartment);
    }

    public DepartmentDto getDepartmentByGuid(String guid) {
        return departmentConverter.convert(departmentRepository.getByGuid(guid));
    }

    public Page<DepartmentDto> getAllDepartments(Pageable pageable) {
        Page<Department> departmentsPage = departmentRepository.findAll(pageable);
        return departmentsPage.map(departmentConverter::convert);
    }

    public Page<DepartmentDto> searchDepartments(String name, EntityStatus entityStatus, Pageable pageable) {
        BooleanBuilder builder = DepartmentQueryBuilder.createQuery(name, entityStatus);
        Page<Department> departmentsPage = departmentRepository.findAll(builder, pageable);
        return departmentsPage.map(departmentConverter::convert);
    }

    public DepartmentDto updateDepartment(String guid, DepartmentCreateRequest request) {
        Optional<Department> duplicatedDepartment = departmentRepository.findByNameIgnoreCaseAndEntityStatus(request.getName(), EntityStatus.ACTIVE);
        if (duplicatedDepartment.isPresent() && !duplicatedDepartment.get().getGuid().equals(guid)) {
            throw new EntityAlreadyExistsException();
        }

        Department departmentFromDb = departmentRepository.getByGuid(guid);
        departmentFromDb.update(request.getName());

        Department updatedDepartment = departmentRepository.save(departmentFromDb);
        log.info("Updated department -> {}", updatedDepartment.getName());
        return departmentConverter.convert(updatedDepartment);
    }

    public void deleteDepartment(String guid) {
        Department departmentFromDb = departmentRepository.getByGuid(guid);
        departmentRepository.delete(departmentFromDb);
        log.info("Deleted department -> {}", departmentFromDb.getName());
    }

    public DepartmentDto activateDepartment(String guid) {
        Department departmentFromDb = departmentRepository.getByGuid(guid);
        departmentRepository.activate(departmentFromDb);
        log.info("Activated department -> {}", departmentFromDb.getName());
        return departmentConverter.convert(departmentFromDb);
    }
}
