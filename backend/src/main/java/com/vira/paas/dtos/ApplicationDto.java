package com.vira.paas.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.vira.paas.enums.ApplicationStatusEnum;

public record ApplicationDto(
        UUID id,
        String name,
        String gitUrl,
        ApplicationStatusEnum status,
        String lastImageTag,
        LocalDateTime createdAt) {
}
