package com.dalal.notificationsservicepfe.services;

import com.dalal.notificationsservicepfe.dtos.event.NotificationEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReservationCreatedEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReservationStatusUpdatedEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReviewCreatedEvent;
import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;
import com.dalal.notificationsservicepfe.entities.Notification;
import com.dalal.notificationsservicepfe.enums.NotificationType;
import com.dalal.notificationsservicepfe.exceptions.ResourceNotFoundException;
import com.dalal.notificationsservicepfe.mappers.NotificationMapper;
import com.dalal.notificationsservicepfe.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;



@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    @Override
    public void createReservationCreatedNotification(ReservationCreatedEvent event) {
        // Guard clause validation
        if (event == null || event.providerId() == null) {
            log.warn("Événement ReservationCreatedEvent invalide ou nul reçu : {}", event);
            return; // Early return mieux qu'exception pour éviter de bloquer le Kafka Listener
        }

        // Business Logic : Création de la notification pour le Prestataire
        Notification notification = Notification.builder()
                .userId(event.providerId())
                .notificationType(NotificationType.BOOKING_CREATED)
                .message("Vous avez reçu une nouvelle demande de réservation (N° " + event.bookingId() + ").")
                .isRead(false)
                .createdAt(event.createdAt() != null ? event.createdAt() : LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Notification BOOKING_CREATED enregistrée pour l'utilisateur ID: {}", event.providerId());
    }

    @Override
    public void createReservationStatusUpdatedNotification(ReservationStatusUpdatedEvent event) {
        // Guard Clause Validation
        if (event == null || event.status() == null) {
            log.warn("Événement ReservationStatusUpdatedEvent invalide ou nul reçu : {}", event);
            return;
        }

        Long targetUserId;
        NotificationType type;
        String message;

        switch (event.status()) {
            case CONFIRMED -> {
                targetUserId = event.clientId();
                type = NotificationType.BOOKING_CONFIRMED;
                message = "Votre réservation (N° " + event.bookingId() + ") a été confirmée par le prestataire.";
            }
            case CANCELLED -> {
                targetUserId = event.providerId();
                type = NotificationType.BOOKING_CANCELLED;
                message = "La réservation (N° " + event.bookingId() + ") a été annulée par le client.";
            }
            case REJECTED -> {
                targetUserId = event.clientId();
                type = NotificationType.BOOKING_REJECTED;
                message = "Votre réservation (N° " + event.bookingId() + ") a été refusée par le prestataire.";
            }
            case COMPLETED -> {
                targetUserId = event.clientId();
                type = NotificationType.BOOKING_COMPLETED;
                message = "Votre prestation pour la réservation (N° " + event.bookingId() + ") est terminée.";
            }
            default -> {
                log.warn("Statut de réservation non pris en charge pour notification : {}", event.status());
                return;
            }
        }

        if (targetUserId == null) {
            log.warn("L'ID de l'utilisateur destinataire est null pour le statut : {}", event.status());
            return;
        }

        Notification notification = Notification.builder()
                .userId(targetUserId)
                .notificationType(type)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Notification {} enregistrée pour l'utilisateur ID: {}", type, targetUserId);
    }

    @Override
    public void createReviewCreatedNotification(ReviewCreatedEvent event) {
        if (event == null || event.providerId() == null) {
            log.warn("Événement ReviewCreatedEvent invalide ou nul reçu : {}", event);
            return;
        }

        Notification notification = Notification.builder()
                .userId(event.providerId())
                .notificationType(NotificationType.REVIEW_RECEIVED)
                .message("Un client vous a laissé un nouvel avis : \"" + event.comment() + "\"")
                .isRead(false)
                .createdAt(event.createdAt() != null ? event.createdAt() : LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        log.info("Notification REVIEW_RECEIVED enregistrée pour le provider ID: {}", event.providerId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId) {
        validateUserId(userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        validateUserId(userId);
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public NotificationResponse markAsRead(Long id) {
        // Guard Clause
        validateId(id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'ID : " + id));

        // Early return si déjà lue (évite un UPDATE inutile en DB)
        if (notification.isRead()) {
            return notificationMapper.toResponseDTO(notification);
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return notificationMapper.toResponseDTO(updatedNotification);
    }

    @Override
    public void deleteNotification(Long id) {
        // Guard Clause
        validateId(id);

        if (!notificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notification non trouvée avec l'ID : " + id);
        }

        notificationRepository.deleteById(id);
    }

    @Override
    public void deleteAllUserNotifications(Long userId) {
        // Guard Clause
        validateUserId(userId);
        notificationRepository.deleteByUserId(userId);
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("L'ID utilisateur doit être un identifiant valide.");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID de la notification doit être un identifiant valide.");
        }
    }
}
