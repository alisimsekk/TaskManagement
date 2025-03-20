package com.alisimsek.taskmanagement.department.service;

import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.common.exception.EntityAlreadyExistsException;
import com.alisimsek.taskmanagement.department.controller.dto.converter.DepartmentConverter;
import com.alisimsek.taskmanagement.department.controller.dto.request.DepartmentCreateRequest;
import com.alisimsek.taskmanagement.department.controller.dto.response.DepartmentDto;
import com.alisimsek.taskmanagement.department.entity.Department;
import com.alisimsek.taskmanagement.department.repository.DepartmentRepository;
import com.querydsl.core.BooleanBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.alisimsek.taskmanagement.department.DepartmentTestProvider.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;
    @Mock
    private DepartmentRepository departmentRepository;
    @Spy
    private DepartmentConverter departmentConverter;

    private Department department = getDepartment();
    private String guid = department.getGuid();
    private DepartmentCreateRequest createRequest = getDepartmentCreateRequest();

    @Test
    void test_createDepartment_success() {
        when(departmentRepository.findByNameAndEntityStatus(createRequest.getName(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());
        when(departmentRepository.save(any())).thenReturn(department);
        DepartmentDto actualResult = departmentService.createDepartment(createRequest);

        assertNotNull(actualResult);
        assertEquals(createRequest.getName(), actualResult.getName());
    }

    @Test
    void test_createDepartment_whenDepartmentExists_shouldThrowException() {
        when(departmentRepository.findByNameAndEntityStatus(DEPARTMENT_NAME, EntityStatus.ACTIVE))
                .thenReturn(Optional.of(department));

        assertThrows(EntityAlreadyExistsException.class, () -> {
            departmentService.createDepartment(createRequest);
        });
        verify(departmentRepository).findByNameAndEntityStatus(DEPARTMENT_NAME, EntityStatus.ACTIVE);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void getDepartmentByGuid_shouldReturnDepartmentDto() {
        when(departmentRepository.getByGuid(guid)).thenReturn(department);

        DepartmentDto actualResult = departmentService.getDepartmentByGuid(guid);

        assertNotNull(actualResult);
        assertEquals(department.getGuid(), actualResult.getGuid());
        verify(departmentRepository).getByGuid(guid);
    }

    @Test
    void getAllDepartments_shouldReturnPageOfDepartmentDtos() {
        List<Department> departments = Collections.singletonList(department);
        Page<Department> departmentsPage = new PageImpl<>(departments);
        Pageable pageable = mock(Pageable.class);

        when(departmentRepository.findAll(pageable)).thenReturn(departmentsPage);

        Page<DepartmentDto> result = departmentService.getAllDepartments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(department.getGuid(), result.getContent().getFirst().getGuid());
        verify(departmentRepository).findAll(pageable);
    }


    @Test
    void searchDepartments_shouldReturnFilteredDepartments() {
        List<Department> departments = Collections.singletonList(department);
        Page<Department> departmentsPage = new PageImpl<>(departments);
        Pageable pageable = mock(Pageable.class);

        when(departmentRepository.findAll(any(BooleanBuilder.class), eq(pageable))).thenReturn(departmentsPage);

        Page<DepartmentDto> result = departmentService.searchDepartments(DEPARTMENT_NAME, EntityStatus.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(department.getGuid(), result.getContent().getFirst().getGuid());
    }

    @Test
    void updateDepartment_whenNoDuplicateName_shouldUpdateSuccessfully() {
        String updatedName = "Updated Name";
        createRequest.setName(updatedName);
        Department updatedDepartment = getDepartment();
        updatedDepartment.setName(updatedName);

        when(departmentRepository.findByNameIgnoreCaseAndEntityStatus(createRequest.getName(), EntityStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(departmentRepository.getByGuid(guid)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(updatedDepartment);

        DepartmentDto actualResult = departmentService.updateDepartment(guid, createRequest);

        assertNotNull(actualResult);
        assertEquals(updatedName, actualResult.getName());
    }

    @Test
    void updateDepartment_whenDuplicateNameForDifferentDepartment_shouldThrowException() {
        Department otherDepartment = getDepartment();

        when(departmentRepository.findByNameIgnoreCaseAndEntityStatus(DEPARTMENT_NAME, EntityStatus.ACTIVE))
                .thenReturn(Optional.of(otherDepartment));

        assertThrows(EntityAlreadyExistsException.class, () -> {
            departmentService.updateDepartment(guid, createRequest);
        });
        verify(departmentRepository).findByNameIgnoreCaseAndEntityStatus(DEPARTMENT_NAME, EntityStatus.ACTIVE);
        verify(departmentRepository, never()).getByGuid(anyString());
    }

    @Test
    void updateDepartment_whenDuplicateNameForSameDepartment_shouldUpdateSuccessfully() {
        String updatedName = "Updated Name";
        createRequest.setName(updatedName);
        Department updatedDepartment = getDepartment();
        updatedDepartment.setName(updatedName);

        when(departmentRepository.findByNameIgnoreCaseAndEntityStatus(updatedName, EntityStatus.ACTIVE))
                .thenReturn(Optional.of(department));
        when(departmentRepository.getByGuid(guid)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(updatedDepartment);

        DepartmentDto actualResult = departmentService.updateDepartment(guid, createRequest);

        assertNotNull(actualResult);
        assertEquals(updatedName, actualResult.getName());
    }

    @Test
    void deleteDepartment_shouldDeleteSuccessfully() {
        when(departmentRepository.getByGuid(guid)).thenReturn(department);

        departmentService.deleteDepartment(guid);

        verify(departmentRepository).delete(department);
    }

    @Test
    void activateDepartment_shouldActivateSuccessfully() {
        when(departmentRepository.getByGuid(guid)).thenReturn(department);

        DepartmentDto actualResult = departmentService.activateDepartment(guid);

        assertNotNull(actualResult);
        verify(departmentRepository).activate(department);
    }
}
