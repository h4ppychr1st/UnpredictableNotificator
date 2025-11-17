package org.example.services.Task3;

import org.example.configs.Task3.ReminderServiceConfig;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ReminderChecker {
    private final ReminderService service;
    private final ReminderServiceConfig reminderServiceConfig;

    public ReminderChecker(ReminderService service, ReminderServiceConfig reminderServiceConfig) {
        this.service = service;
        this.reminderServiceConfig = reminderServiceConfig;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Thread.ofVirtual()
                .name("reminder-worker")
                .start(() -> {
                    while (true) {
                        try {
                            service.processExpiredReminders();
                            Thread.sleep(Duration.ofSeconds(reminderServiceConfig.timeoutInSeconds()));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
