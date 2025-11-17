package org.example.controllers;

import org.example.services.ActionDispatcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(produces = APPLICATION_JSON_VALUE,
                consumes = APPLICATION_JSON_VALUE)
public class PostDataController {
    private final ActionDispatcher actionDispatcher;

    public PostDataController(ActionDispatcher actionDispatcher) {
        this.actionDispatcher = actionDispatcher;
    }

    @PostMapping(value = "/actions")
    public ResponseEntity<String> processPostNewAction(@RequestBody ActionDTO action) throws Exception {
        actionDispatcher.processNewAction(action);
        return ResponseEntity.ok("Action registered!");
    }

    public record ActionDTO(int userId, String actionType, long timestamp) {}
}
