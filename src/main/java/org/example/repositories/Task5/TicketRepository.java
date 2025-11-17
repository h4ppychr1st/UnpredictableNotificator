package org.example.repositories.Task5;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TicketRepository {
    private final DataSource dataSource;

    public TicketRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void saveRecord(int userId, Instant instant) throws SQLException {
        String sql = """
            INSERT INTO tickets (user_id, ts)
            VALUES (?, ?)
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.from(instant));
            ps.executeUpdate();
        }
    }

    public List<Long> findUnnotifiedTicketsForLastMonth(int userId) throws SQLException {
        String sql = """
            SELECT id
            FROM tickets
            WHERE user_id = ?
              AND notified = false
              AND ts >= now() - interval '1 month'
            ORDER BY ts DESC
            """;

        List<Long> ids = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong("id"));
                }
            }
        }
        return ids;
    }

    public void markNotifiedTickets(List<Long> ids) throws SQLException {
        if (ids.isEmpty())
            return;

        String placeholders = ids.stream()
                .map(i -> "?")
                .collect(Collectors.joining(","));

        String sql = "UPDATE tickets SET notified = true WHERE id IN (" + placeholders + ")";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            for (Long id : ids) {
                ps.setLong(index++, id);
            }
            ps.executeUpdate();
        }
    }
}
