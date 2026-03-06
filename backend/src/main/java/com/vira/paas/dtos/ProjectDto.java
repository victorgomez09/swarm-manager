package com.vira.paas.dtos;

import java.util.UUID;

public record ProjectDto(
        UUID id,
        String name,
        String description) {

}
