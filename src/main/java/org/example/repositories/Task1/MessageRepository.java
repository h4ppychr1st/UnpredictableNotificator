package org.example.repositories.Task1;

import org.example.configs.Task1.MessageServiceConfig;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.example.services.Task1.MessageService.zone;

@Repository
public class MessageRepository {
    private final DataSource dataSource;
    private final byte nightStart;
    private final byte nightEnd;

    public MessageRepository(DataSource dataSource, MessageServiceConfig messageServiceConfig) {
        this.dataSource = dataSource;
        nightStart = messageServiceConfig.nightStart();
        nightEnd = messageServiceConfig.nightEnd();
    }

    public void saveRecord(int userId, Instant timestamp) throws SQLException{
        String sql = """
                INSERT INTO messages (user_id, ts)
                VALUES (?, ?)
                """;

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setTimestamp(2, Timestamp.from(timestamp));
            preparedStatement.executeUpdate();
        }
    }

    public boolean hasMessagesInBothPreviousNights(int userId, LocalDate twoDaysBefore, LocalDate oneDayBefore) throws SQLException {
        LocalDateTime startTwoDaysBefore = twoDaysBefore.atTime(nightStart, 0);
        LocalDateTime endTwoDaysBefore   = twoDaysBefore.plusDays(1).atTime(nightEnd, 0);
        LocalDateTime startOneDayBefore = oneDayBefore.atTime(nightStart, 0);
        LocalDateTime endOneDayBefore   = oneDayBefore.plusDays(1).atTime(nightEnd, 0);

        String sql = """
        SELECT
            (EXISTS (
                SELECT 1 FROM messages
                WHERE user_id = ?
                  AND ts >= ? AND ts < ?
            )) AS has_two_days_before,
            (EXISTS (
                SELECT 1 FROM messages
                WHERE user_id = ?
                  AND ts >= ? AND ts < ?
            )) AS has_one_day_before
        """;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setTimestamp(2, Timestamp.from(startTwoDaysBefore.atZone(zone).toInstant()));
            ps.setTimestamp(3, Timestamp.from(endTwoDaysBefore.atZone(zone).toInstant()));
            ps.setLong(4, userId);
            ps.setTimestamp(5, Timestamp.from(startOneDayBefore.atZone(zone).toInstant()));
            ps.setTimestamp(6, Timestamp.from(endOneDayBefore.atZone(zone).toInstant()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean hasTwoDaysBefore = rs.getBoolean("has_two_days_before");
                    boolean has_one_day_before = rs.getBoolean("has_one_day_before");
                    return hasTwoDaysBefore && has_one_day_before;
                }
            }
        }
        return false;
    }
}
