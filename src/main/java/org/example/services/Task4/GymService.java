package org.example.services.Task4;

import org.example.configs.Task4.GymServiceConfig;
import org.example.repositories.Task4.GymRepository;
import org.example.repositories.Task4.GymRepository.Lesson;
import org.example.services.ProcessorService;
import org.example.services.notifications.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.example.utils.Constants.GYM;

@Service
public class GymService implements ProcessorService {
    private final GymRepository gymRepository;
    private final NotificationService notifier;
    private final GymServiceConfig gymServiceConfig;
    private final Duration minLesson;
    private final Duration maxLesson;
    private final double differenceRatioForNotification;

    public GymService(GymRepository gymRepository, NotificationService notifier, GymServiceConfig gymServiceConfig) {
        this.gymRepository = gymRepository;
        this.notifier = notifier;
        this.gymServiceConfig = gymServiceConfig;
        minLesson = Duration.ofMinutes(gymServiceConfig.minLessonDurationInMinutes());
        maxLesson = Duration.ofMinutes(gymServiceConfig.maxLessonDurationInMinutes());
        differenceRatioForNotification = 1 + gymServiceConfig.percentageDifferenceForNotification() * 0.01;
    }

    @Override
    @Transactional
    public void processAction(int userId, long timestamp) throws SQLException {
        Instant instant = Instant.ofEpochMilli(timestamp);
        Lesson currSession = gymRepository.findLastUnfinishedLesson(userId);

        if (currSession == null) {
            gymRepository.saveRecord(userId, instant);
            return;
        }

        gymRepository.updateEnd(currSession.id(), instant);
        Duration currSessionDuration = Duration.between(currSession.startedAt(), instant);

        if (currSessionDuration.compareTo(minLesson) < 0) {
            System.out.printf("Session of user %d was too short so we can't count it", userId);
            gymRepository.deleteLesson(currSession.id());
            return;
        }

        if (currSessionDuration.compareTo(maxLesson) > 0) {
            System.out.printf("Session of user %d last too long or he forgot to end it...", userId);
            gymRepository.deleteLesson(currSession.id());
            return;
        }

        Lesson previousLesson = gymRepository.findLastFinished(userId);
        if (previousLesson != null) {
            Duration previousLessonDuration = Duration.between(previousLesson.startedAt(), previousLesson.endedAt());
            double ratio = (double) currSessionDuration.toMinutes() / previousLessonDuration.toMinutes();
            if (ratio >= differenceRatioForNotification) {
                notifier.logNotification(userId, GYM, instant);
            }
        }
    }

    @Transactional
    public void checkUnfinishedAndIdle() throws SQLException {
        Instant nowMinusTwoHours = Instant.now().minus(2, ChronoUnit.HOURS);
        for (long idleLesson : gymRepository.findUnfinishedAndStartedBefore(nowMinusTwoHours)) {
            System.out.printf("Session %d last too long or he forgot to end it...", idleLesson);
            gymRepository.deleteLesson(idleLesson);
        }
    }

    public int getTimeoutInSeconds() {
        return gymServiceConfig.timeoutInSeconds();
    }
}
