package org.example.controllers;

import org.example.exceptions.NotificationException;
import org.example.services.ActionDispatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE)
public class GetDataController {
    private final ActionDispatcher actionDispatcher;

    public GetDataController(ActionDispatcher actionDispatcher) {
        this.actionDispatcher = actionDispatcher;
    }

    @GetMapping(value = "/notifications")
    public ResponseEntity<String> processGetAllNotifications() throws NotificationException {
        return ResponseEntity.ok(actionDispatcher.getAllNotifications());
    }
}

