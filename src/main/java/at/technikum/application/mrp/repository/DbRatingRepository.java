package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbRatingRepository implements RatingRepository {
    private final ConnectionPool connectionPool;

    public DbRatingRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Rating> findById(String id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Rating> findByMediaId(String mediaId) {
        String sql = "SELECT * FROM ratings WHERE media_id = ?";
        List<Rating> ratings = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public List<Rating> findByUserId(String userId) {
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        List<Rating> ratings = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public Optional<Rating> findByMediaIdAndUserId(String mediaId, String userId) {
        String sql = "SELECT * FROM ratings WHERE media_id = ? AND user_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mediaId);
            stmt.setString(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRating(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Rating> findAll() {
        String sql = "SELECT * FROM ratings";
        List<Rating> ratings = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ratings.add(mapResultSetToRating(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ratings;
    }

    @Override
    public Rating save(Rating rating) {
        String sql = "INSERT INTO ratings (id, media_id, user_id, stars, comment, timestamp, confirmed, likes) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rating.getId());
            stmt.setString(2, rating.getMediaId());
            stmt.setString(3, rating.getUserId());
            stmt.setInt(4, rating.getStars());
            stmt.setString(5, rating.getComment());
            stmt.setTimestamp(6, Timestamp.valueOf(rating.getCreatedAt()));
            stmt.setBoolean(7, rating.isConfirmed());
            stmt.setInt(8, rating.getLikes());
            stmt.executeUpdate();
            return rating;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rating update(Rating rating) {
        String sql = "UPDATE ratings SET stars = ?, comment = ?, confirmed = ?, likes = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, rating.getStars());
            stmt.setString(2, rating.getComment());
            stmt.setBoolean(3, rating.isConfirmed());
            stmt.setInt(4, rating.getLikes());
            stmt.setString(5, rating.getId());
            stmt.executeUpdate();
            return rating;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM ratings WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Rating mapResultSetToRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getString("id"));
        rating.setMediaId(rs.getString("media_id"));
        rating.setUserId(rs.getString("user_id"));
        rating.setStars(rs.getInt("stars"));
        rating.setComment(rs.getString("comment"));
        rating.setCreatedAt(rs.getTimestamp("timestamp").toLocalDateTime());
        rating.setConfirmed(rs.getBoolean("confirmed"));
        rating.setLikes(rs.getInt("likes"));
        return rating;
    }
}