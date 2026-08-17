package com.dalal.notificationsservicepfe.repositories;

import com.dalal.notificationsservicepfe.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 1. Consulter la liste des notifications par utilisateur (triées par la plus récente)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 2. Compter les notifications non lues pour le badge
    long countByUserIdAndIsReadFalse(Long userId);

    // 3. Supprimer toutes les notifications d'un utilisateur
    void deleteByUserId(Long userId);
}