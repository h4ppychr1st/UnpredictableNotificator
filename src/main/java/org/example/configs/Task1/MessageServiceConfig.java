package org.example.configs.Task1;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.message")
public class MessageServiceConfig {
    private byte morningStart;
    private byte nightStart;
    private byte nightEnd;

    public byte nightEnd() {
        return nightEnd;
    }

    public void setNightEnd(byte nightEnd) {
        this.nightEnd = nightEnd;
    }

    public byte nightStart() {
        return nightStart;
    }

    public void setNightStart(byte nightStart) {
        this.nightStart = nightStart;
    }

    public byte morningStart() {
        return morningStart;
    }

    public void setMorningStart(byte morningStart) {
        this.morningStart = morningStart;
    }
}
