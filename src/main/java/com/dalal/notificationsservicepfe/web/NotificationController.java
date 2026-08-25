package com.dalal.notificationsservicepfe.web;

import com.dalal.notificationsservicepfe.handler.ErrorResponse;
import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;
import com.dalal.notificationsservicepfe.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Endpoints de gestion des notifications utilisateurs")
@PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_PROVIDER')") // on peut aussi utiliser hasAnyRole
// as long as we have the same condition for roles we can put it on the class level
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Consulter les notifications d'un utilisateur",
            description = "Récupère toutes les notifications triées de la plus récente à la plus ancienne.")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")

    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Compter les notifications non lues",
            description = "Retourne le nombre total de notifications non lues pour afficher le badge.")
    @ApiResponse(responseCode = "200", description = "Nombre de notifications non lues récupéré")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marquer une notification comme lue",
            description = "Met à jour le statut isRead de la notification à true.")
    @ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    @ApiResponse(responseCode = "404", description = "Notification non trouvée",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une notification",
            description = "Supprime une notification spécifique par son ID.")
    @ApiResponse(responseCode = "204", description = "Notification supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Notification non trouvée",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Supprimer toutes les notifications d'un utilisateur",
            description = "Vide complètement la liste des notifications de l'utilisateur.")
    @ApiResponse(responseCode = "204", description = "Toutes les notifications ont été supprimées")
    public ResponseEntity<Void> deleteAllUserNotifications(@PathVariable Long userId) {
        notificationService.deleteAllUserNotifications(userId);
        return ResponseEntity.noContent().build();
    }
}