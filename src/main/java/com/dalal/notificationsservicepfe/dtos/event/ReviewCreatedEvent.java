package com.dalal.notificationsservicepfe.dtos.event;

import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ReviewCreatedEvent(
        Long reviewId,
        Long reservationId,
        Long clientId,
        Long providerId,
        Boolean isRecommended,
        String comment,
        LocalDateTime createdAt
) {
}
