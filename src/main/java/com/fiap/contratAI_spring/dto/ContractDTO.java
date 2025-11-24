package com.fiap.contratAI_spring.dto;

import java.time.LocalDateTime;

public record ContractDTO(
        Long id,
        String title,
        String content,
        String status,
        LocalDateTime createdAt,
        Long ownerId,
        String ownerName,
        String ownerEmail
) {}
