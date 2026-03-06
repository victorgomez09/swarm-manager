package com.vira.paas.mappers;

import org.mapstruct.Mapper;

import com.vira.paas.dtos.ProjectDto;
import com.vira.paas.models.ProjectModel;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDto toDto(ProjectModel model);

    ProjectModel toModel(ProjectDto dto);
}
