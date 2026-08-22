package com.dalal.notificationsservicepfe.services;

import com.dalal.notificationsservicepfe.dtos.event.ReservationCreatedEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReservationStatusUpdatedEvent;
import com.dalal.notificationsservicepfe.dtos.event.ReviewCreatedEvent;
import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Traiter et enregistrer une notification lors de la création d'une réservation
     */
    void createReservationCreatedNotification(ReservationCreatedEvent event);

    /**
     * Traiter et enregistrer une notification lors du changement de statut d'une réservation
     */
    void createReservationStatusUpdatedNotification(ReservationStatusUpdatedEvent event);

    /**
     * Traiter et enregistrer une notification lors de la création d'un avis/review
     */
    void createReviewCreatedNotification(ReviewCreatedEvent event);

    /**
     * Obtenir toutes les notifications d'un utilisateur spécifique (Client ou Prestataire)
     */
    List<NotificationResponse> getUserNotifications(Long userId);

    /**
     * Obtenir le nombre de notifications non lues (pour le badge)
     */
    long getUnreadCount(Long userId);

    /**
     * Marquer une notification spécifique comme lue
     */
    NotificationResponse markAsRead(Long id);

    /**
     * Supprimer une seule notification
     */
    void deleteNotification(Long id);

    /**
     * Supprimer toutes les notifications d'un utilisateur
     */
    void deleteAllUserNotifications(Long userId);
}