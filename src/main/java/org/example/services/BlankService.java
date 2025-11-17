package org.example.services;

import org.example.exceptions.NotificationException;

public class BlankService implements ProcessorService {
    private final String actionType;

    public BlankService(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public void processAction(int userId, long timestamp) throws NotificationException {
        throw new NotificationException("Зарегистрирован тип неизвестного сообщения - " + actionType);
    }
}
