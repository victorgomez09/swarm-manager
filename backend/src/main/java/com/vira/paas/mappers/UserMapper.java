package com.vira.paas.mappers;

import org.mapstruct.Mapper;

import com.vira.paas.dtos.UserDto;
import com.vira.paas.models.UserModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(UserModel model);

    UserModel toModel(UserDto dto);
}
