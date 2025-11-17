package at.technikum.application.mrp.repository;

import at.technikum.application.common.ConnectionPool;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.MediaType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DbMediaRepository implements MediaRepository {
    private final ConnectionPool connectionPool;

    public DbMediaRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Optional<Media> findById(String id) {
        String sql = "SELECT * FROM media WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Media> findAll() {
        String sql = "SELECT * FROM media";
        List<Media> mediaList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                mediaList.add(mapResultSetToMedia(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public List<Media> findByTitle(String title) {
        String sql = "SELECT * FROM media WHERE title ILIKE ?";
        List<Media> mediaList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + title + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public List<Media> findByGenre(String genre) {
        String sql = "SELECT * FROM media WHERE ? = ANY(genres)";
        List<Media> mediaList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, genre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public List<Media> findByMediaType(MediaType mediaType) {
        String sql = "SELECT * FROM media WHERE media_type = ?";
        List<Media> mediaList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mediaType.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public List<Media> findByReleaseYear(int year) {
        String sql = "SELECT * FROM media WHERE release_year = ?";
        List<Media> mediaList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public List<Media> findByAgeRestriction(int maxAge) {
        String sql = "SELECT * FROM media WHERE age_restriction <= ?";
        List<Media> mediaList = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, maxAge);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mediaList.add(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return mediaList;
    }

    @Override
    public Media save(Media media) {
        String sql = "INSERT INTO media (id, title, description, media_type, release_year, genres, age_restriction, creator_id, average_rating, rating_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getId());
            stmt.setString(2, media.getTitle());
            stmt.setString(3, media.getDescription());
            stmt.setString(4, media.getMediaType().name());
            stmt.setInt(5, media.getReleaseYear());
            Array genresArray = conn.createArrayOf("text", media.getGenres().toArray());
            stmt.setArray(6, genresArray);
            stmt.setInt(7, media.getAgeRestriction());
            stmt.setString(8, media.getCreatorId());
            stmt.setDouble(9, media.getAverageRating());
            stmt.setInt(10, media.getRatingCount());
            stmt.executeUpdate();
            return media;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Media update(Media media) {
        String sql = "UPDATE media SET title = ?, description = ?, media_type = ?, release_year = ?, genres = ?, age_restriction = ?, average_rating = ?, rating_count = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getDescription());
            stmt.setString(3, media.getMediaType().name());
            stmt.setInt(4, media.getReleaseYear());
            Array genresArray = conn.createArrayOf("text", media.getGenres().toArray());
            stmt.setArray(5, genresArray);
            stmt.setInt(6, media.getAgeRestriction());
            stmt.setDouble(7, media.getAverageRating());
            stmt.setInt(8, media.getRatingCount());
            stmt.setString(9, media.getId());
            stmt.executeUpdate();
            return media;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM media WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        Media media = new Media();
        media.setId(rs.getString("id"));
        media.setTitle(rs.getString("title"));
        media.setDescription(rs.getString("description"));
        media.setMediaType(MediaType.valueOf(rs.getString("media_type")));
        media.setReleaseYear(rs.getInt("release_year"));

        Array genresArray = rs.getArray("genres");
        if (genresArray != null) {
            String[] genres = (String[]) genresArray.getArray();
            media.setGenres(Arrays.asList(genres));
        }

        media.setAgeRestriction(rs.getInt("age_restriction"));
        media.setCreatorId(rs.getString("creator_id"));
        media.setAverageRating(rs.getDouble("average_rating"));
        media.setRatingCount(rs.getInt("rating_count"));
        return media;
    }
}