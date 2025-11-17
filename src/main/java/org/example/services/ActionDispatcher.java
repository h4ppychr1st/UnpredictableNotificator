package org.example.services;

import org.example.controllers.PostDataController.ActionDTO;
import org.example.exceptions.NotificationException;
import org.example.services.Task1.MessageService;
import org.example.services.Task2.CoffeeService;
import org.example.services.Task3.ReminderService;
import org.example.services.Task4.GymService;
import org.example.services.Task5.TicketService;
import org.example.services.notifications.NotificationService;
import org.example.utils.enums.ActionType;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

import static org.example.utils.enums.ActionType.getActionType;

@Service
public class ActionDispatcher {
    private final MessageService messageService;
    private final CoffeeService coffeeService;
    private final ReminderService reminderService;
    private final GymService gymService;
    private final TicketService ticketService;
    private final NotificationService notificationService;

    public ActionDispatcher(MessageService messageService,
                            CoffeeService coffeeService,
                            ReminderService reminderService,
                            GymService gymService,
                            TicketService ticketService,
                            NotificationService notificationService) {
        this.messageService = messageService;
        this.coffeeService = coffeeService;
        this.reminderService = reminderService;
        this.gymService = gymService;
        this.ticketService = ticketService;
        this.notificationService = notificationService;
    }

    public void processNewAction(ActionDTO requestBody) throws Exception {
        int userId = requestBody.userId();
        long timestamp = requestBody.timestamp();
        int reminderDuration = ActionType.getMinutesIfReminder(requestBody.actionType());
        if (reminderDuration > 0) {
            reminderService.processAction(userId, timestamp, reminderDuration);
            return;
        }

        ActionType actionType = getActionType(requestBody.actionType());
        ProcessorService processorService = switch (actionType) {
            case MESSAGE -> messageService;
            case BUY_COFFEE -> coffeeService;
            case GYM -> gymService;
            case TICKET -> ticketService;
            case BLANK -> new BlankService(requestBody.actionType());
        };

        try {
            processorService.processAction(userId, timestamp);
        } catch (SQLException e) {
            throw new NotificationException("Something went wrong with DB: " + e.getMessage());
        }
    }

    public String getAllNotifications() throws NotificationException {
        try {
            return notificationService.getAll();
        } catch (SQLException e) {
            throw new NotificationException("Something went wrong with DB: " + e.getMessage());
        }
    }
}
