package com.alisimsek.taskmanagement.task.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.common.base.EntityStatus;
import com.alisimsek.taskmanagement.project.entity.Project;
import com.alisimsek.taskmanagement.task.entity.Task;
import com.alisimsek.taskmanagement.task.entity.TaskPriority;
import com.alisimsek.taskmanagement.task.entity.TaskState;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends BaseRepository<Task, Long> {
    Optional<Task> findByTitleIgnoreCaseAndProjectAndEntityStatus(String title, Project project, EntityStatus entityStatus);

    Optional<Task> findByTitleIgnoreCaseAndPriorityAndProjectAndEntityStatus(String title, TaskPriority priority, Project project, EntityStatus entityStatus);
}
