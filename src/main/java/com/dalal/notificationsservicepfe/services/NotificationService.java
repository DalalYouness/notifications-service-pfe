
import com.dalal.notificationsservicepfe.dtos.event.NotificationEvent;
import com.dalal.notificationsservicepfe.dtos.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Process and save incoming notification event from Kafka broker
     */
    void processNotificationEvent(NotificationEvent event);

    /**
     * Get all notifications for a specific user (Client or Prestataire)
     */
    List<NotificationResponse> getUserNotifications(Long userId);

    /**
     * Get count of unread notifications for badge display
     */
    long getUnreadCount(Long userId);

    /**
     * Mark a specific notification as read
     */
    NotificationResponse markAsRead(Long id);

    /**
     * Delete a single notification
     */
    void deleteNotification(Long id);

    /**
     * Delete all notifications for a specific user
     */
    void deleteAllUserNotifications(Long userId);
}