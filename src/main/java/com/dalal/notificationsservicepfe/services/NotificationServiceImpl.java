package com.dalal.notificationsservicepfe.services;

import com.dalal.notificationsservicepfe.dtos.event.NotificationEvent;
import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;
import com.dalal.notificationsservicepfe.entities.Notification;
import com.dalal.notificationsservicepfe.exceptions.ResourceNotFoundException;
import com.dalal.notificationsservicepfe.mappers.NotificationMapper;
import com.dalal.notificationsservicepfe.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public void processNotificationEvent(NotificationEvent event) {
        /* Guard Clause : Validation de l'événement Kafka
        Guard Clause : Validation manuelle pour les messages Kafka
        Contrairement aux requêtes HTTP gérées par les Controllers (où @Valid/@NotNull fonctionnent automatiquement),
        les messages provenant d'un Kafka Broker ne passent pas par la couche de validation Spring Web.
        Cette vérification protège le Consumer contre les exceptions 'NullPointerException' (Poison Pills)
        et évite le blocage du traitement des messages en file d'attente.*/
        if (event == null || event.userId() == null || event.type() == null) {
            log.warn("Événement de notification invalide ou incomplet reçu : {}", event);
            return;
        }

        log.info("Traitement de la notification pour l'utilisateur ID: {}", event.userId());

        Notification notification = notificationMapper.toEntity(event);
        Notification savedNotification = notificationRepository.save(notification);

        // TODO: Notification en temps réel (WebSocket / SSE) pour éviter le refresh au niveau client
        log.info("Notification sauvegardée avec succès avec l'ID: {}", savedNotification.getId());
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
