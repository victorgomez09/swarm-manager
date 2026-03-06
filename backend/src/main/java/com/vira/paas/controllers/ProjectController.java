package com.vira.paas.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vira.paas.dtos.ProjectDto;
import com.vira.paas.mappers.ProjectMapper;
import com.vira.paas.models.UserModel;
import com.vira.paas.services.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService service;
    private final ProjectMapper mapper;

    @GetMapping
    public ResponseEntity<List<ProjectDto>> findAll(@AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(service.findAllByUser(user).stream().map(mapper::toDto).toList());
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("@access.canAccessProject(#projectId)")
    public ResponseEntity<ProjectDto> findById(@PathVariable UUID projectId, @AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(mapper.toDto(service.findById(projectId)));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(@RequestBody ProjectDto project, @AuthenticationPrincipal UserModel user) {
        return ResponseEntity.ok(mapper.toDto(service.create(mapper.toModel(project), user)));
    }

    @PostMapping("/{projectId}/members/{userId}")
    @PreAuthorize("@access.canAccessProject(#projectId)")
    public void addMember(@PathVariable UUID projectId, @PathVariable UUID userId) {
    }
}