package com.alisimsek.taskmanagement.user.repository;

import com.alisimsek.taskmanagement.common.base.BaseRepository;
import com.alisimsek.taskmanagement.user.entity.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default Optional<User> findByUsernameInNewTransaction(String username) {
        return findByUsername(username);
    }

}
