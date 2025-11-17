package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UnpredictableNotificator {
    public static void main(String[] args) {
        try {
            SpringApplication.run(UnpredictableNotificator.class, args);
        } catch (Throwable t) {
            System.exit(0);
        }
    }
}