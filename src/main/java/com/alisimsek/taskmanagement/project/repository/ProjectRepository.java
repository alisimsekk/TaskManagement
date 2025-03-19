package com.alisimsek.taskmanagement.project.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.entity.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends BaseRepository<Project, Long> {
    Optional<Project> findByTitleIgnoreCaseAndEntityStatus(String title, EntityStatus entityStatus);

    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE LOWER(p.title) = LOWER(:title) AND p.entityStatus = :status AND p.guid <> :guid")
    boolean existsByTitleAndStatusExceptGuid(String title, EntityStatus status, String guid);
}
