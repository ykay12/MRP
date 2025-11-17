package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.Favorite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbFavoriteRepository implements FavoriteRepository {
    private final ConnectionPool connectionPool;

    public DbFavoriteRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Favorite> findById(String id) {
        String sql = "SELECT * FROM favorites WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFavorite(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Favorite> findByUserId(String userId) {
        String sql = "SELECT * FROM favorites WHERE user_id = ?";
        List<Favorite> favorites = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    favorites.add(mapResultSetToFavorite(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return favorites;
    }

    @Override
    public Optional<Favorite> findByUserIdAndMediaId(String userId, String mediaId) {
        String sql = "SELECT * FROM favorites WHERE user_id = ? AND media_id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, mediaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFavorite(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Favorite save(Favorite favorite) {
        String sql = "INSERT INTO favorites (id, user_id, media_id) VALUES (?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, favorite.getId());
            stmt.setString(2, favorite.getUserId());
            stmt.setString(3, favorite.getMediaId());
            stmt.executeUpdate();
            return favorite;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM favorites WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Favorite mapResultSetToFavorite(ResultSet rs) throws SQLException {
        Favorite favorite = new Favorite();
        favorite.setId(rs.getString("id"));
        favorite.setUserId(rs.getString("user_id"));
        favorite.setMediaId(rs.getString("media_id"));
        return favorite;
    }
}