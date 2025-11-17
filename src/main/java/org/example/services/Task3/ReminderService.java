package org.example.services.Task3;

import org.example.repositories.Task3.ReminderRepository;
import org.example.services.notifications.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.example.utils.Constants.REMINDER;

@Service
public class ReminderService {
    private final ReminderRepository reminderRepository;
    private final NotificationService notifier;

    public ReminderService(ReminderRepository reminderRepository, NotificationService notifier) {
        this.reminderRepository = reminderRepository;
        this.notifier = notifier;
    }

    @Transactional
    public void processAction(int userId, long timestamp, int reminderDuration) throws SQLException {
        reminderRepository.saveRecord(userId, Instant.ofEpochMilli(timestamp).plus(reminderDuration, ChronoUnit.MINUTES));
    }

    @Transactional
    public void processExpiredReminders() throws SQLException {
        List<Integer> users = reminderRepository.findUsersWithExpiredTime();
        for (int userId : users) {
            notifier.logNotification(userId, REMINDER, Instant.now());
        }
    }
}
