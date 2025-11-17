package org.example.services.Task4;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LessonChecker {
    private final GymService service;

    public LessonChecker(GymService service) {
        this.service = service;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Thread.ofVirtual()
                .name("gym-worker")
                .start(() -> {
                    while (true) {
                        try {
                            service.checkUnfinishedAndIdle();
                            Thread.sleep(Duration.ofSeconds(service.getTimeoutInSeconds()));
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
