package com.vira.paas.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vira.paas.models.UserModel;

public interface UserRepository extends JpaRepository<UserModel, UUID> {

    boolean existsByUsername(String username);

    Optional<UserModel> findByUsername(String username);
}
