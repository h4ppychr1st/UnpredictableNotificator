package org.example.repositories.Task1;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OwlRepository {
    private final DataSource dataSource;

    public OwlRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void markAsOwl(int userId, LocalDate candidateFor) throws SQLException {
        String sql = """
            INSERT INTO owls (user_id, candidate_for)
            VALUES (?, ?)
            ON CONFLICT (user_id) DO UPDATE
            SET candidate_for = excluded.candidate_for,
                notified_at   = NULL
            """;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(candidateFor));
            ps.executeUpdate();
        }
    }

    public List<Integer> getOwlsForYesterday(LocalDate date) throws SQLException {
        LocalDate yesterdayNight = date.minusDays(1);
        String sql = """
            SELECT user_id
            FROM owls
            WHERE candidate_for = ? AND notified_at IS NULL
            """;

        List<Integer> list = new ArrayList<>();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(yesterdayNight));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("user_id"));
                }
            }
        }
        return list;
    }

    public void markNotified(int userId) throws SQLException {
        String sql = "UPDATE owls SET notified_at = now() WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
