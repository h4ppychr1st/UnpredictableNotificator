package org.example.repositories.Task3;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReminderRepository {
    private final DataSource dataSource;

    public ReminderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveRecord(int userId, Instant instant) throws SQLException {
        String sql = """
            INSERT INTO reminders (user_id, remind_at)
            VALUES (?, ?)
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.from(instant));
            ps.executeUpdate();
        }
    }

    public List<Integer> findUsersWithExpiredTime() throws SQLException {
        String sql = """
        DELETE FROM reminders
        WHERE remind_at <= now()
        RETURNING user_id
        """;

        List<Integer> sent = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sent.add(rs.getInt("user_id"));
            }
        }
        return sent;
    }
}
