package com.vira.paas.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vira.paas.models.ProjectModel;
import com.vira.paas.models.UserModel;

public interface ProjectRepository extends JpaRepository<ProjectModel, UUID> {

    @Query("SELECT p FROM ProjectModel p LEFT JOIN p.members m WHERE p.id = :projectId AND m.username = :username")
    boolean findByUsernameInMembers(UUID projectId, String username);

    @Query("SELECT p FROM ProjectModel p LEFT JOIN p.members m WHERE m = :user")
    List<ProjectModel> findAllByMembers(UserModel user);
}
