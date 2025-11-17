package org.example.repositories.Task4;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GymRepository {
    private final DataSource dataSource;

    public GymRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Lesson findLastUnfinishedLesson(int userId) throws SQLException {
        String sql = """
            SELECT id, user_id, started_at, ended_at
            FROM lessons
            WHERE user_id = ? AND ended_at IS NULL
            ORDER BY started_at DESC
            LIMIT 1
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Lesson(
                            rs.getLong("id"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("started_at").toInstant(),
                            null);
                }
            }
        }
        return null;
    }

    public List<Long> findUnfinishedAndStartedBefore(Instant instant) throws SQLException {
        String sql = """
                DELETE
                FROM lessons
                WHERE ended_at IS NULL
                  AND started_at < ?
                RETURNING id;
                """;

        List<Long> idleLessons = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.from(instant));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    idleLessons.add(rs.getLong("id"));
                }
            }
        }
        return idleLessons;
    }

    public void saveRecord(int userId, Instant startTime) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO lessons (user_id, started_at) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setTimestamp(2, Timestamp.from(startTime));
            ps.executeUpdate();
        }
    }

    public void updateEnd(long id, Instant endTime) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE lessons SET ended_at = ? WHERE id = ?")) {
            ps.setTimestamp(1, Timestamp.from(endTime));
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deleteLesson(long id) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM lessons WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public Lesson findLastFinished(int userId) throws SQLException {
        String sql = """
            SELECT id, started_at, ended_at
            FROM lessons
            WHERE user_id = ? AND ended_at IS NOT NULL
            ORDER BY ended_at DESC
            LIMIT 1
            OFFSET 1
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Lesson(
                            rs.getLong("id"),
                            userId,
                            rs.getTimestamp("started_at").toInstant(),
                            rs.getTimestamp("ended_at").toInstant());
                }
            }
        }
        return null;
    }

    public record Lesson(long id, int userId, Instant startedAt, Instant endedAt) {}
}
