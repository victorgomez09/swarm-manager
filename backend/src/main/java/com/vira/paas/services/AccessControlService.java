package com.vira.paas.services;

import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vira.paas.repositories.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service("access")
@RequiredArgsConstructor
public class AccessControlService {
    private final ProjectRepository projectRepo;

    public boolean canAccessProject(UUID projectId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return projectRepo.findByUsernameInMembers(projectId, username);
    }

    // public boolean canAccessApp(UUID appId) {
    //     String username = SecurityContextHolder.getContext().getAuthentication().getName();

    //     return projectRepo.userOwnerOfApp(appId, username);
    // }
}