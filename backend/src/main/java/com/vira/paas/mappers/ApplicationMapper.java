package com.vira.paas.mappers;

import org.mapstruct.Mapper;

import com.vira.paas.dtos.ApplicationDto;
import com.vira.paas.models.ApplicationModel;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationModel toEntity(ApplicationDto dto);

    ApplicationDto toDto(ApplicationModel model);
}
