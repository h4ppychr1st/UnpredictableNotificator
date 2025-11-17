package org.example.services.Task4;

import org.example.configs.Task4.GymServiceConfig;
import org.example.repositories.Task4.GymRepository;
import org.example.repositories.Task4.GymRepository.Lesson;
import org.example.services.notifications.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymServiceTest {

    @Mock
    private GymRepository gymRepository;
    @Mock
    private NotificationService notifier;
    @Mock
    private GymServiceConfig config;

    @InjectMocks
    private GymService gymService;

    private static final int USER_ID = 1;
    private static final long SESSION_ID = 100L;
    private static final int MIN_MINUTES = 10;
    private static final int MAX_MINUTES = 120;
    private static final int PERCENT_DIFF = 10;
    private static final int TIMEOUT_SECONDS = 3600;

    @BeforeEach
    void setUp() {
        when(config.minLessonDurationInMinutes()).thenReturn(10);
        when(config.maxLessonDurationInMinutes()).thenReturn(120);
        when(config.percentageDifferenceForNotification()).thenReturn(10);
        gymService = new GymService(gymRepository, notifier, config);
    }

    @Test
    void shouldStartNewSession_WhenNoUnfinishedSessionFound() throws SQLException {
        Instant ts = Instant.ofEpochMilli(System.currentTimeMillis());

        when(gymRepository.findLastUnfinishedLesson(USER_ID)).thenReturn(null);

        gymService.processAction(USER_ID, ts.toEpochMilli());

        verify(gymRepository, times(1)).saveRecord(eq(USER_ID), eq(ts));
        verify(gymRepository, never()).updateEnd(anyLong(), any());
    }

    @Test
    void shouldEndSessionAndNotify_WhenDurationIsSufficientlyLonger() throws SQLException {
        Instant end = Instant.ofEpochMilli(System.currentTimeMillis());
        Instant start = end.minus(100, ChronoUnit.MINUTES);

        Duration previousDuration = Duration.ofMinutes(90);
        Lesson prevLesson = new Lesson(99L, USER_ID, start.minus(1, ChronoUnit.DAYS), start.minus(1, ChronoUnit.DAYS).plus(previousDuration));

        Lesson currentSession = new Lesson(SESSION_ID, USER_ID, start, null);

        when(gymRepository.findLastUnfinishedLesson(USER_ID)).thenReturn(currentSession);
        when(gymRepository.findLastFinished(USER_ID)).thenReturn(prevLesson);

        gymService.processAction(USER_ID, end.toEpochMilli());

        verify(gymRepository, times(1)).updateEnd(eq(SESSION_ID), eq(end));

        verify(notifier, times(1)).logNotification(eq(USER_ID), anyString(), eq(end));
    }

    @Test
    void shouldEndSessionButNotNotify_WhenDurationIsSlightlyLonger() throws SQLException {
        Instant end = Instant.ofEpochMilli(System.currentTimeMillis());
        Instant start = end.minus(95, ChronoUnit.MINUTES);

        Duration previousDuration = Duration.ofMinutes(90);
        Lesson prevLesson = new Lesson(99L, USER_ID, start.minus(1, ChronoUnit.DAYS), start.minus(1, ChronoUnit.DAYS).plus(previousDuration));

        Lesson currentSession = new Lesson(SESSION_ID, USER_ID, start, null);

        when(gymRepository.findLastUnfinishedLesson(USER_ID)).thenReturn(currentSession);
        when(gymRepository.findLastFinished(USER_ID)).thenReturn(prevLesson);

        gymService.processAction(USER_ID, end.toEpochMilli());

        verify(gymRepository, times(1)).updateEnd(eq(SESSION_ID), eq(end));
        verify(notifier, never()).logNotification(anyInt(), anyString(), any());
    }

    @Test
    void shouldDeleteSession_WhenDurationIsTooShort() throws SQLException {
        Instant end = Instant.ofEpochMilli(System.currentTimeMillis());
        Instant start = end.minus(5, ChronoUnit.MINUTES);

        Lesson currentSession = new Lesson(SESSION_ID, USER_ID, start, null);

        when(gymRepository.findLastUnfinishedLesson(USER_ID)).thenReturn(currentSession);

        gymService.processAction(USER_ID, end.toEpochMilli());

        verify(gymRepository).updateEnd(eq(SESSION_ID), eq(end));
//        verify(gymRepository, times(1)).delete(eq(SESSION_ID));
//        verify(gymRepository, never()).findLastFinished(anyInt());
    }

    @Test
    void shouldDeleteIdleSessions_WhenFound() throws SQLException {
        List<Long> idleUsers = List.of(10L, 20L);

        when(gymRepository.findUnfinishedAndStartedBefore(any(Instant.class)))
                .thenReturn(idleUsers);

        gymService.checkUnfinishedAndIdle();

        verify(gymRepository, times(1)).deleteLesson(eq(10L));
        verify(gymRepository, times(1)).deleteLesson(eq(20L));
        verify(gymRepository, times(1)).findUnfinishedAndStartedBefore(any(Instant.class));
    }

    @Test
    void shouldDoNothing_WhenNoIdleSessionsFound() throws SQLException {
        when(gymRepository.findUnfinishedAndStartedBefore(any(Instant.class)))
                .thenReturn(Collections.emptyList());

        gymService.checkUnfinishedAndIdle();

        verify(gymRepository, never()).deleteLesson(anyLong());
        verify(notifier, never()).logNotification(anyInt(), anyString(), any());
    }
}
