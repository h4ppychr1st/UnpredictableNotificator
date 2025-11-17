package org.example.services.notifications;

import org.example.configs.NotificationTemplates;
import org.example.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.example.repositories.NotificationRepository.Notification;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationTemplates notificationTemplates;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationTemplates notificationTemplates) {
        this.notificationRepository = notificationRepository;
        this.notificationTemplates = notificationTemplates;
    }

    public void logNotification(int userId, String type, Instant instant) throws SQLException {
        String message = notificationTemplates.getTemplates().get(type);
        notificationRepository.saveRecord(userId, message, instant);
    }

    public String getAll() throws SQLException {
        return jsonFromNotifications(notificationRepository.findAll());
    }

    public String jsonFromNotifications(List<Notification> notifications) {
        StringBuilder result = new StringBuilder("{");
        for (Notification notification : notifications) {
            result.append("\"").append(notification.id()).append("\"").append(":");
            result.append(String.format("{\"userId\": %d, \"message\": \"%s\", \"ts\": %d}",
                    notification.userId(),
                    notification.message(),
                    notification.timestamp().toEpochMilli()));
            result.append(",");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("}");
        return result.toString();
    }
}
