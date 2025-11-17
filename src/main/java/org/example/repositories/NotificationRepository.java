package org.example.repositories;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotificationRepository {

    private final DataSource dataSource;

    public NotificationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveRecord(int userId, String message, Instant instant) throws SQLException {
        String sql = """
            INSERT INTO notifications (user_id, message, ts)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.setTimestamp(3, Timestamp.from(instant));
            ps.executeUpdate();
        }
    }

    public List<Notification> findAll() throws SQLException {
        String sql = """
                SELECT id, user_id, message, ts
                FROM notifications
                ORDER BY ts
                DESC
                """;

        List<Notification> notifications = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(new Notification(
                            rs.getLong("id"),
                            rs.getInt("user_id"),
                            rs.getString("message"),
                            rs.getTimestamp("ts").toInstant()
                    ));
                }
            }
        }
        return notifications;
    }

    public record Notification(long id, int userId, String message, Instant timestamp) {}
}
