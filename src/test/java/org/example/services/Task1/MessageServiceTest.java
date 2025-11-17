package org.example.services.Task1;

import org.example.configs.Task1.MessageServiceConfig;
import org.example.repositories.Task1.MessageRepository;
import org.example.repositories.Task1.OwlRepository;
import org.example.services.notifications.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.example.services.ProcessorService.zone;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private OwlRepository owlRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private MessageServiceConfig MessageServiceConfig;
    private MessageService handler;
    private static final int USER_ID = 123;

    // Фиксированные моменты времени для теста
    private static final Instant NIGHT1 = ZonedDateTime.of(2025, 11, 12, 23, 30, 0, 0, zone).toInstant(); // ср → чт
    private static final Instant NIGHT2 = ZonedDateTime.of(2025, 11, 13, 23, 45, 0, 0, zone).toInstant(); // чт → пт
    private static final Instant NIGHT3 = ZonedDateTime.of(2025, 11, 14, 2, 15, 0, 0, zone).toInstant();  // пт → сб (02:15)
    private static final Instant MORNING = ZonedDateTime.of(2025, 11, 15, 9, 0, 0, 0, zone).toInstant();   // сб утро

    @BeforeEach
    void setUp() {
        // Создаём handler с моками
        handler = new MessageService(owlRepository, messageRepository, notificationService, MessageServiceConfig);
    }

    @Test
    void shouldMarkAsCandidateAndSendEmail_WhenThreeConsecutiveNights() throws SQLException {
        whenBothPreviousNightsHaveMessages(true);
        handler.handleNewMessage(USER_ID, NIGHT3);

        // Ожидаем, что кандидат помечен за ночь с пятницы на субботу (2025-11-14)
        verify(owlRepository).markAsOwl(USER_ID, LocalDate.of(2025, 11, 13));

        // === Утро субботы — кто-то пишет днём ===
        when(owlRepository.getOwlsForYesterday(LocalDate.ofInstant(MORNING, zone))).thenReturn(List.of(USER_ID));

        handler.handleNewMessage(USER_ID, MORNING);

        // === ПРОВЕРКА ===
        verify(owlRepository).markNotified(USER_ID);
    }

    @Test
    void shouldNotMarkAsCandidate_IfOnlyTwoNights() throws SQLException {
        whenBothPreviousNightsHaveMessages(false);
        handler.handleNewMessage(USER_ID, NIGHT1);

        whenBothPreviousNightsHaveMessages(false); // нет в позапозавчера
        handler.handleNewMessage(USER_ID, NIGHT3);

        verifyNoInteractions(owlRepository); // кандидат не помечен
    }

    // Вспомогательный метод — мок для проверки двух предыдущих ночей
    private void whenBothPreviousNightsHaveMessages(boolean hasMessages) throws SQLException {
        // Мокаем PUBLIC метод репозитория — просто и красиво
        when(messageRepository.hasMessagesInBothPreviousNights(
                eq(USER_ID), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(hasMessages);
    }
}