package org.example.configs.Task4;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.gym")
public class GymServiceConfig {
    private int timeoutInSeconds;
    private int minLessonDurationInMinutes;
    private int maxLessonDurationInMinutes;
    private int percentageDifferenceForNotification;

    public int percentageDifferenceForNotification() {
        return percentageDifferenceForNotification;
    }

    public void setPercentageDifferenceForNotification(int percentageDifferenceForNotification) {
        this.percentageDifferenceForNotification = percentageDifferenceForNotification;
    }

    public int maxLessonDurationInMinutes() {
        return maxLessonDurationInMinutes;
    }

    public void setMaxLessonDurationInMinutes(int maxLessonDurationInMinutes) {
        this.maxLessonDurationInMinutes = maxLessonDurationInMinutes;
    }

    public int minLessonDurationInMinutes() {
        return minLessonDurationInMinutes;
    }

    public void setMinLessonDurationInMinutes(int minLessonDurationInMinutes) {
        this.minLessonDurationInMinutes = minLessonDurationInMinutes;
    }

    public int timeoutInSeconds() {
        return timeoutInSeconds;
    }

    public void setTimeoutInSeconds(int timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
    }
}
