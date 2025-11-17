package org.example.repositories.Task2;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;

@Repository
public class CoffeeDiscountRepository {
    private final DataSource dataSource;

    public CoffeeDiscountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public RewardRecord getDiscountByUserId(int userId) throws SQLException {
        String sql = """
                SELECT reward_date, coupon_used
                FROM rewards
                WHERE user_id = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new RewardRecord(
                            rs.getDate("reward_date").toLocalDate(),
                            rs.getBoolean("coupon_used")
                    );
                }
            }
        }
        return null;
    }

    public void issueNewCoupon(int userId, LocalDate date) throws SQLException {
        String sql = """
                  INSERT INTO rewards (user_id, reward_date, coupon_used)
                  VALUES (?, ?, false)
                  ON CONFLICT (user_id) DO UPDATE SET
                  reward_date = excluded.reward_date,
                  coupon_used = false
            """;
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        }
    }

    public void markCouponUsed(int userId) throws SQLException {
        String sql = """
                UPDATE rewards
                SET coupon_used = true
                WHERE user_id = ? AND coupon_used = false
                """;
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public record RewardRecord(LocalDate rewardDate, boolean used) {}
}
