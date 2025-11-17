package org.example.services.Task2;

import org.example.configs.Task2.CoffeeServiceConfig;
import org.example.repositories.Task2.CoffeeDiscountRepository;
import org.example.repositories.Task2.CoffeeOrderRepository;
import org.example.services.notifications.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.example.services.ProcessorService.zone;
import static org.example.utils.Constants.COFFEE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoffeeServiceTest {

    @Mock
    private CoffeeOrderRepository coffeeOrderRepository;
    @Mock
    private CoffeeDiscountRepository coffeeDiscountRepository;
    @Mock
    private NotificationService notifier;
    @Mock
    private CoffeeServiceConfig config;

    private CoffeeService service;


    @BeforeEach
    void setUp() {
        when(config.daysOfWeekToCheck()).thenReturn(List.of(1, 3, 5));
        when(config.dayOfWeekOfDiscount()).thenReturn(2);
        service = new CoffeeService(
                coffeeOrderRepository,
                coffeeDiscountRepository,
                notifier,
                config
        );
    }

    @Test
    void shouldIssueCoupon_WhenAllThreeDaysPresent() throws SQLException {
        int userId = 100;
        long timestamp = LocalDate.of(2025, 8, 11)
                .atStartOfDay(zone).toInstant().toEpochMilli();

        when(coffeeDiscountRepository.getDiscountByUserId(userId)).thenReturn(null);
        when(coffeeOrderRepository.hasCoffeeOrdersInAllDays(anyInt(), any(LocalDate.class), anyList()))
                .thenReturn(true);

        service.processAction(userId, timestamp);

        verify(coffeeDiscountRepository).issueNewCoupon(userId, LocalDate.of(2025, 8, 11));
        verify(notifier).logNotification(userId, COFFEE, Instant.ofEpochMilli(timestamp));
    }

    @Test
    void shouldNotIssueCoupon_WhenCouponAlreadyActive() throws SQLException {
        int userId = 100;
        long timestamp = LocalDate.of(2025, 8, 13).atStartOfDay(zone).toInstant().toEpochMilli();

        CoffeeDiscountRepository.RewardRecord activeCoupon = new CoffeeDiscountRepository.RewardRecord(
                LocalDate.now().minusDays(5),
                false
        );

        when(coffeeDiscountRepository.getDiscountByUserId(userId)).thenReturn(activeCoupon);

        service.processAction(userId, timestamp);

        verify(coffeeDiscountRepository, never()).issueNewCoupon(anyInt(), any());
        verify(notifier, never()).logNotification(anyInt(), anyString(), any());
    }

    @Test
    void shouldMarkCouponUsed_WhenTuesdayAction() throws SQLException {
        int userId = 100;
        long timestamp = LocalDate.of(2025, 8, 12).atStartOfDay(zone).toInstant().toEpochMilli();

        service.processAction(userId, timestamp);

        verify(coffeeDiscountRepository).markCouponUsed(userId);
        verifyNoMoreInteractions(coffeeDiscountRepository);
        verifyNoInteractions(notifier);
    }

    @Test
    void shouldDoNothing_WhenWrongDayOfWeek() throws SQLException {
        int userId = 100;
        long timestamp = LocalDate.of(2025, 8, 10).atStartOfDay(zone).toInstant().toEpochMilli();

        service.processAction(userId, timestamp);

        verify(coffeeOrderRepository).saveOrder(userId, Instant.ofEpochMilli(timestamp));
        verifyNoMoreInteractions(coffeeOrderRepository);
        verifyNoInteractions(coffeeDiscountRepository);
        verifyNoInteractions(notifier);
    }

    @Test
    void shouldIssueCouponAfterCouponWasUsed() throws SQLException {
        int userId = 100;
        long timestamp = LocalDate.of(2025, 8, 18).atStartOfDay(zone).toInstant().toEpochMilli();

        CoffeeDiscountRepository.RewardRecord usedCoupon = new CoffeeDiscountRepository.RewardRecord(
                LocalDate.now().minusDays(10),
                true
        );

        when(coffeeDiscountRepository.getDiscountByUserId(userId)).thenReturn(usedCoupon);
        when(coffeeOrderRepository.hasCoffeeOrdersInAllDays(eq(userId), any(LocalDate.class), anyList()))
                .thenReturn(true);

        service.processAction(userId, timestamp);

        verify(coffeeDiscountRepository).issueNewCoupon(userId, LocalDate.of(2025, 8, 18));
        verify(notifier).logNotification(userId, COFFEE, Instant.ofEpochMilli(timestamp));
    }
}