package org.example.services.Task5;

import org.example.configs.Task5.TicketServiceConfig;
import org.example.repositories.Task5.TicketRepository;
import org.example.services.notifications.NotificationService;
import org.example.services.ProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.example.utils.Constants.TICKET;

@Service
public class TicketService implements ProcessorService {
    private final TicketRepository ticketRepository;
    private final NotificationService notifier;
    private final int tripsForDiscount;

    public TicketService(TicketRepository ticketRepository,
                         NotificationService notifier,
                         TicketServiceConfig ticketServiceConfig) {
        this.ticketRepository = ticketRepository;
        this.notifier = notifier;
        tripsForDiscount = ticketServiceConfig.monthlyTripsForDiscount();
    }

    @Override
    @Transactional
    public void processAction(int userId, long timestamp) throws SQLException {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ticketRepository.saveRecord(userId, instant);
        handleNewMessage(userId, instant);
    }

    public void handleNewMessage(int userId, Instant ts) throws SQLException {
        List<Long> unnotifiedIds = ticketRepository.findUnnotifiedTicketsForLastMonth(userId);
        if (unnotifiedIds.size() >= tripsForDiscount) {
            ticketRepository.markNotifiedTickets(unnotifiedIds);
            notifier.logNotification(userId, TICKET, ts);
        }
    }
}
