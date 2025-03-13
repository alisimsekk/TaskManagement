package com.alisimsek.taskmanagement.department.entity;

import com.alisimsek.taskmanagement.common.base.BaseEntity;
import com.alisimsek.taskmanagement.project.entity.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Department extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Project> projects;

    public static Department create(String name) {
        Department department = new Department();
        department.name = name;
        return department;
    }

    public void update(String name) {
        this.name = name;
    }
}
