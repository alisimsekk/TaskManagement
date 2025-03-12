package com.alisimsek.taskmanagement.common.base;

import com.alisimsek.taskmanagement.common.exception.EntityNotFoundException;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID extends Long> extends JpaRepository<T, ID >, QuerydslPredicateExecutor<T> {

    default T getById(ID id) {
        return findById(id)
                .orElseThrow(entityNotFound());
    }

    Optional<T> findByGuid(String guid);

    Optional<T> findByIdAndEntityStatus(ID id, EntityStatus entityStatus);

    Optional<T> findByGuidAndEntityStatus(String guid, EntityStatus entityStatus);

    default T getByGuid(String guid) {
        return findByGuid(guid)
                .orElseThrow(entityNotFound());
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

    default Supplier<EntityNotFoundException> entityNotFound() {
        return () -> {
            Object[] args = new Object[1];
            args[0] = getEntityClassName();
            return new EntityNotFoundException(args);
        };
    }

    default String getEntityClassName() {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(getClass(), BaseRepository.class);
        return classes[0].getSimpleName();
    }
}
