package org.example.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.notifications")
public class NotificationTemplates {
    private Map<String, String> templates = new HashMap<>();

    public Map<String, String> getTemplates() {
        return templates;
    }
    public void setTemplates(Map<String, String> templates) {
        this.templates = templates;
    }
}
