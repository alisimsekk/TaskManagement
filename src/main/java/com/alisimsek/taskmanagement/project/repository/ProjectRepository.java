package com.alisimsek.taskmanagement.project.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.project.entity.Project;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends BaseRepository<Project, Long> {
}
