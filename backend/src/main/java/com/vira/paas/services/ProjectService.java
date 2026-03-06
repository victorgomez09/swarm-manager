package com.vira.paas.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.vira.paas.models.ProjectModel;
import com.vira.paas.models.UserModel;
import com.vira.paas.repositories.ProjectRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;

    public List<ProjectModel> findAllByUser(UserModel user) {
        return repository.findAllByMembers(user);
    }

    public ProjectModel findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    public ProjectModel create(ProjectModel model, UserModel owner) {
        model.setOwner(owner);

        return repository.save(model);
    }

}
