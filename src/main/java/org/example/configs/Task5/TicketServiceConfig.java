package org.example.configs.Task5;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ticket")
public class TicketServiceConfig {
    private int monthlyTripsForDiscount;

    public int monthlyTripsForDiscount() {
        return monthlyTripsForDiscount;
    }

    public void setMonthlyTripsForDiscount(int monthlyTripsForDiscount) {
        this.monthlyTripsForDiscount = monthlyTripsForDiscount;
    }
}
