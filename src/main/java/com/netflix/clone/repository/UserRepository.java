package com.netflix.clone.repository;

import com.netflix.clone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional <User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User>findByVerificationToken(String verificationToken);
    Optional <User> findByPasswordResetToken(String passwordResetToken);
}
