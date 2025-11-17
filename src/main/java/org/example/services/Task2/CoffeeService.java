package org.example.services.Task2;

import org.example.configs.Task2.CoffeeServiceConfig;
import org.example.repositories.Task2.CoffeeDiscountRepository;
import org.example.repositories.Task2.CoffeeDiscountRepository.RewardRecord;
import org.example.repositories.Task2.CoffeeOrderRepository;
import org.example.services.ProcessorService;
import org.example.services.notifications.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.example.utils.Constants.COFFEE;

@Service
public class CoffeeService implements ProcessorService {
    private final CoffeeOrderRepository coffeeOrderRepository;
    private final CoffeeDiscountRepository coffeeDiscountRepository;
    private final NotificationService notifier;
    private final List<Integer> daysOfWeekToCheck;
    private final int dayOfWeekOfDiscount;

    public CoffeeService(CoffeeOrderRepository coffeeOrderRepository,
                         CoffeeDiscountRepository coffeeDiscountRepository,
                         NotificationService notifier,
                         CoffeeServiceConfig coffeeServiceConfig) {
        this.coffeeOrderRepository = coffeeOrderRepository;
        this.coffeeDiscountRepository = coffeeDiscountRepository;
        this.notifier = notifier;
        daysOfWeekToCheck = coffeeServiceConfig.daysOfWeekToCheck();
        dayOfWeekOfDiscount = coffeeServiceConfig.dayOfWeekOfDiscount();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processAction(int userId, long timestamp) throws SQLException {
        Instant instant = Instant.ofEpochMilli(timestamp);
        coffeeOrderRepository.saveOrder(userId, instant);
        handleNewMessage(userId, instant);
    }

    public void handleNewMessage(int userId, Instant instant) throws SQLException {
        ZonedDateTime zdt = instant.atZone(zone);
        DayOfWeek dayOfWeek = zdt.getDayOfWeek();
        LocalDate date = zdt.toLocalDate();

        RewardRecord activeCoupon = coffeeDiscountRepository.getDiscountByUserId(userId);
        if (activeCoupon != null && !activeCoupon.used()) {
            if (dayOfWeek.getValue() == dayOfWeekOfDiscount) {
                coffeeDiscountRepository.markCouponUsed(userId);
                return;
            }
            return;
        }

        if (!daysOfWeekToCheck.contains(dayOfWeek.getValue())) {
            return;
        }

        LocalDate checkFrom = (activeCoupon == null) ? LocalDate.of(1970, 1, 1) : activeCoupon.rewardDate().plusDays(1);
        if (coffeeOrderRepository.hasCoffeeOrdersInAllDays(userId, checkFrom, daysOfWeekToCheck)) {
            coffeeDiscountRepository.issueNewCoupon(userId, date);
            notifier.logNotification(userId, COFFEE, instant);
        }
    }
}
