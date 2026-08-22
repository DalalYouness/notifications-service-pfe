package com.dalal.notificationsservicepfe.dtos.event;

import com.dalal.notificationsservicepfe.enums.BookingStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationStatusUpdatedEvent(
        Long bookingId,
        Long clientId,
        Long providerId,
        BookingStatus status,
        LocalDateTime createdAt
) {
}
