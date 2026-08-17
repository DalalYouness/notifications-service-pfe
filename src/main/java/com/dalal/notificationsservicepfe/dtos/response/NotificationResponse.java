package com.dalal.notificationsservicepfe.dtos.response;

import com.dalal.notificationsservicepfe.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long id,
        String message,
        boolean isRead,
        NotificationType notificationType,
        LocalDateTime createdAt,
        Long userId
) {
}
