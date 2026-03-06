package com.vira.paas.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.vira.paas.dtos.ApplicationDto;
import com.vira.paas.mappers.ApplicationMapper;
import com.vira.paas.models.ApplicationModel;
import com.vira.paas.services.ApplicationService;
import com.vira.paas.services.BuilderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/apps")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final BuilderService buildService;
    private final ApplicationMapper mapper;

    @PostMapping
    @PreAuthorize("@access.canAccessProject(#projectId)")
    public ResponseEntity<ApplicationDto> createApp(@RequestBody ApplicationDto appRequest) {
        ApplicationModel app = applicationService.create(mapper.toEntity(appRequest));
        buildService.processBuild(app);

        return ResponseEntity.accepted().body(mapper.toDto(app));
    }

    @GetMapping
    public List<ApplicationDto> getAll() {
        return applicationService.findAll().stream().map(mapper::toDto).toList();
    }
}