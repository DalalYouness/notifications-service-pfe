package com.dalal.notificationsservicepfe.messaging;

import com.dalal.notificationsservicepfe.dtos.event.ReservationCreatedEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReservationStatusUpdatedEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReviewCreatedEvent;
import com.dalal.notificationsservicepfe.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "reservation-created-topic",
            properties = {
                    "spring.json.value.default.type=com.dalal.notificationsservicepfe.dtos.event.ReservationCreatedEvent"
            }
    )
    public void consumeReservationCreated(ReservationCreatedEvent event) {
        log.info("Received ReservationCreatedEvent for booking ID: {}", event.bookingId());
        notificationService.createReservationCreatedNotification(event);
    }

    @KafkaListener(
            topics = "reservation-status-updated-topic",
            properties = {
                    "spring.json.value.default.type=com.dalal.notificationsservicepfe.dtos.event.ReservationStatusUpdatedEvent"
            }
    )
    public void consumeReservationStatusUpdated(ReservationStatusUpdatedEvent event) {
        log.info("Received ReservationStatusUpdatedEvent for booking ID: {} with status: {}",
                event.bookingId(), event.status());
        notificationService.createReservationStatusUpdatedNotification(event);
    }

    @KafkaListener(
            topics = "review-created-topic",
            properties = {
                    "spring.json.value.default.type=com.dalal.notificationsservicepfe.dtos.event.ReviewCreatedEvent"
            }
    )
    public void consumeReviewCreated(ReviewCreatedEvent event) {
        log.info("Received ReviewCreatedEvent for Provider ID: {}", event.providerId());
        notificationService.createReviewCreatedNotification(event);
    }
}