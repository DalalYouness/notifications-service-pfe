package com.dalal.notificationsservicepfe.dtos.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationCreatedEvent(
        Long bookingId,
        Long clientId,
        Long providerId,
        BookingStatus status,
        LocalDateTime bookingDate,
        LocalDateTime createdAt
) {

}
