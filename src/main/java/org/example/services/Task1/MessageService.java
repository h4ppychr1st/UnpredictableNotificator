package org.example.services.Task1;

import org.example.configs.Task1.MessageServiceConfig;
import org.example.repositories.Task1.MessageRepository;
import org.example.repositories.Task1.OwlRepository;
import org.example.services.ProcessorService;
import org.example.services.notifications.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.example.utils.Constants.MESSAGE;

@Service
public class MessageService implements ProcessorService {

    private final OwlRepository owlRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notifier;
    private final LocalTime morningStart;
    private final LocalTime nightStart;
    private final LocalTime nightEnd;
    private volatile LocalDate lastSentDate = null;

    public MessageService(OwlRepository owlRepository,
                          MessageRepository messageRepository,
                          NotificationService notifier,
                          MessageServiceConfig messageServiceConfig) {
        this.owlRepository = owlRepository;
        this.messageRepository = messageRepository;
        this.notifier = notifier;
        morningStart = LocalTime.of(messageServiceConfig.morningStart(), 0);
        nightStart = LocalTime.of(messageServiceConfig.nightStart(), 0);
        nightEnd = LocalTime.of(messageServiceConfig.nightEnd(), 0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processAction(int userId, long timestamp) throws SQLException {
        Instant instant = Instant.ofEpochMilli(timestamp);
        messageRepository.saveRecord(userId, instant);
        handleNewMessage(userId, instant);
    }

    public void handleNewMessage(int userId, Instant instant) throws SQLException {
        ZonedDateTime zdt = instant.atZone(zone);
        LocalTime time = zdt.toLocalTime();
        LocalDate date = zdt.toLocalDate();

        if (isNightTime(time)) {
            LocalDate currentNightDate = time.isBefore(nightEnd)
                    ? date.minusDays(1)
                    : date;

            LocalDate twoDaysBefore = currentNightDate.minusDays(2);
            LocalDate oneDayBefore = currentNightDate.minusDays(1);
            if (messageRepository.hasMessagesInBothPreviousNights(userId, twoDaysBefore, oneDayBefore)) {
                owlRepository.markAsOwl(userId, currentNightDate);
            }
            return;
        }

        if (!time.isBefore(morningStart)) {
            if (date.equals(lastSentDate)) {
                return;
            }
            sendMorningNotifications(zdt);
            lastSentDate = date;
        }
    }

    private boolean isNightTime(LocalTime time) {
        return !time.isBefore(nightStart) || time.isBefore(nightEnd);
    }

    private void sendMorningNotifications(ZonedDateTime zonedDateTime) throws SQLException {
        List<Integer> owls = owlRepository.getOwlsForYesterday(zonedDateTime.toLocalDate());

        for (int userId : owls) {
            notifier.logNotification(userId, MESSAGE, zonedDateTime.toInstant());
            owlRepository.markNotified(userId);
        }
    }
}
