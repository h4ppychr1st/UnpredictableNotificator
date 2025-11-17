package org.example.exceptions;

import org.example.controllers.GetDataController;
import org.example.controllers.PostDataController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = {GetDataController.class, PostDataController.class})
public class NotificationAdvice {
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<String> handleException(NotificationException e) {
        return ResponseEntity.internalServerError().body("Couldn't get response: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.internalServerError().body("Unexpected exception: " + e.getMessage());
    }
}
