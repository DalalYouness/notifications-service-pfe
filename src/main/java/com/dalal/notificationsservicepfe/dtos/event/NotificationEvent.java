package com.dalal.notificationsservicepfe.dtos.event;

import com.dalal.notificationsservicepfe.enums.NotificationType;
import lombok.Builder;

@Builder
public record NotificationEvent(
         Long userId,
         String message,
         NotificationType type
) {
}
