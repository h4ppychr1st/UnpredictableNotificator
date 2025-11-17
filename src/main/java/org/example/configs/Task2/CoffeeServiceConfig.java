package org.example.configs.Task2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.coffee")
public class CoffeeServiceConfig {
    private List<Integer> daysOfWeekToCheck;
    private int dayOfWeekOfDiscount;

    public int dayOfWeekOfDiscount() {
        return dayOfWeekOfDiscount;
    }

    public void setDayOfWeekOfDiscount(int dayOfWeekOfDiscount) {
        this.dayOfWeekOfDiscount = dayOfWeekOfDiscount;
    }

    public List<Integer> daysOfWeekToCheck() {
        return daysOfWeekToCheck;
    }

    public void setDaysOfWeekToCheck(List<Integer> daysOfWeekToCheck) {
        this.daysOfWeekToCheck = daysOfWeekToCheck;
    }
}
