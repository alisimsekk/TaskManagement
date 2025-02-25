package com.alisimsek.taskmanagement.common.baserepository;

import com.alisimsek.taskmanagement.common.baseentity.BaseEntity;
import com.alisimsek.taskmanagement.common.baseentity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T extends BaseEntity, ID extends Long> extends JpaRepository<T, ID > {

    default T getById(ID id) {
        return findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found")); //TODO replace with custom exception.
    }

    Optional<T> findByGuid(String guid);

    Optional<T> findByIdAndEntityStatus(ID id, EntityStatus entityStatus);

    Optional<T> findByGuidAndEntityStatus(String guid, EntityStatus entityStatus);

    default T getByGuid(String guid) {
        return findByGuid(guid)
                .orElseThrow(() -> new RuntimeException("Entity not found")); //TODO replace with custom exception.
    }

    List<T> findAllByEntityStatus(EntityStatus entityStatus);

    default void activate(T entity) {
        if (EntityStatus.DELETED.equals(entity.getEntityStatus())) {
            entity.activate();
            save(entity);
        }
    }

    default void delete(T entity) {
        if (EntityStatus.ACTIVE.equals(entity.getEntityStatus())) {
            entity.delete();
            save(entity);
        }
    }
}
