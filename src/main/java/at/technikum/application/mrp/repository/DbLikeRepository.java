package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.Like;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbLikeRepository implements LikeRepository {
    private final ConnectionPool connectionPool;

    public DbLikeRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Like> findById(String id) {
        String sql = "SELECT * FROM likes WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLike(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Like> findByRatingId(String ratingId) {
        String sql = "SELECT * FROM likes WHERE rating_id = ?";
        List<Like> likes = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ratingId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    likes.add(mapResultSetToLike(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return likes;
    }

    @Override
    public Optional<Like> findByUserIdAndRatingId(String userId, String ratingId) {
        String sql = "SELECT * FROM likes WHERE user_id = ? AND rating_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, ratingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLike(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Like save(Like like) {
        String sql = "INSERT INTO likes (id, user_id, rating_id) VALUES (?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like.getId());
            stmt.setString(2, like.getUserId());
            stmt.setString(3, like.getRatingId());
            stmt.executeUpdate();
            return like;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM likes WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countByRatingId(String ratingId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE rating_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ratingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    private Like mapResultSetToLike(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setId(rs.getString("id"));
        like.setUserId(rs.getString("user_id"));
        like.setRatingId(rs.getString("rating_id"));
        return like;
    }
}