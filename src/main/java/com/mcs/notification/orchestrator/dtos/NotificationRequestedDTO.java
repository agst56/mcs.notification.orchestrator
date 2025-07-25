package com.mcs.notification.orchestrator.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mcs.notification.orchestrator.dtos.enums.NotificationCategory;

import java.time.LocalDateTime;


public record NotificationRequestedDTO(
        Long messageId,
        NotificationCategory category,
        String body,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime createdAt
) {}
