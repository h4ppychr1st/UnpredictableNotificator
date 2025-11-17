package org.example.repositories.Task2;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.services.ProcessorService.zone;

@Repository
public class CoffeeOrderRepository {
    private final DataSource dataSource;

    public CoffeeOrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveOrder(int userId, Instant instant) throws SQLException {
        String sql = "INSERT INTO coffee_orders (user_id, ts) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setTimestamp(2, Timestamp.from(instant));
            ps.executeUpdate();
        }
    }

    public boolean hasCoffeeOrdersInAllDays(
            int userId,
            LocalDate checkFrom,
            List<Integer> days) throws SQLException {
        if (days == null || days.isEmpty()) {
            return true;
        }

        String placeholders = days.stream()
                .map(d -> "?")
                .collect(Collectors.joining(", "));

        String sql = """
        SELECT COUNT(DISTINCT EXTRACT(DOW FROM ts AT TIME ZONE 'Europe/Moscow')) AS matched_days
        FROM coffee_orders
        WHERE user_id = ?
          AND ts >= ?
          AND EXTRACT(DOW FROM ts AT TIME ZONE 'Europe/Moscow') IN (%s)
        """.formatted(placeholders);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.from(checkFrom.atStartOfDay(zone).toInstant()));
            for (int i = 0; i < days.size(); i++) {
                ps.setInt(3 + i, days.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int matchedDays = rs.getInt("matched_days");
                    return matchedDays == days.size();
                }
            }
        }
        return false;
    }
}
