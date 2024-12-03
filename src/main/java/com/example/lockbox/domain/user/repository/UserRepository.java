package com.example.lockbox.domain.user.repository;

import com.example.lockbox.common.exception.NotFoundException;
import com.example.lockbox.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new NotFoundException("User를 찾을 수 없습니다."));
    }
}
