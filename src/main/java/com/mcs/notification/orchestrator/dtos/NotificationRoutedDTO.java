package com.mcs.notification.orchestrator.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mcs.notification.orchestrator.dtos.enums.NotificationCategory;
import com.mcs.notification.orchestrator.dtos.enums.NotificationChannel;

import java.time.LocalDateTime;

public record NotificationRoutedDTO(
        Long messageId,
        NotificationCategory category,
        String body,
        Long userId,
        String userName,
        NotificationChannel channel,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime createdAt
) {
}
