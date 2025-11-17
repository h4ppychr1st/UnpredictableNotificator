package org.example.services;

import java.time.ZoneId;

public interface ProcessorService {
    ZoneId zone = ZoneId.of("Europe/Moscow");

    void processAction(int userId, long timestamp) throws Exception;
}
