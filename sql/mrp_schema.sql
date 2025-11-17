-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(255),
    total_ratings INTEGER DEFAULT 0,
    average_score DOUBLE PRECISION DEFAULT 0.0,
    favorite_genre VARCHAR(255)
    );

-- Media table
CREATE TABLE IF NOT EXISTS media (
                                     id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_type VARCHAR(50) NOT NULL,
    release_year INTEGER NOT NULL,
    genres TEXT[] NOT NULL,
    age_restriction INTEGER NOT NULL,
    creator_id VARCHAR(36) NOT NULL,
    average_rating DOUBLE PRECISION DEFAULT 0.0,
    rating_count INTEGER DEFAULT 0,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Ratings table
CREATE TABLE IF NOT EXISTS ratings (
                                       id VARCHAR(36) PRIMARY KEY,
    media_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    stars INTEGER NOT NULL CHECK (stars >= 1 AND stars <= 5),
    comment TEXT,
    timestamp TIMESTAMP NOT NULL,
    confirmed BOOLEAN DEFAULT FALSE,
    likes INTEGER DEFAULT 0,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE (media_id, user_id)
    );

-- Favorites table
CREATE TABLE IF NOT EXISTS favorites (
                                         id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    media_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE,
    UNIQUE (user_id, media_id)
    );

-- Likes table
CREATE TABLE IF NOT EXISTS likes (
                                     id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    rating_id VARCHAR(36) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE CASCADE,
    UNIQUE (user_id, rating_id)
    );

-- Indexes for better performance
CREATE INDEX IF NOT EXISTS idx_media_title ON media(title);
CREATE INDEX IF NOT EXISTS idx_media_type ON media(media_type);
CREATE INDEX IF NOT EXISTS idx_media_year ON media(release_year);
CREATE INDEX IF NOT EXISTS idx_media_creator ON media(creator_id);
CREATE INDEX IF NOT EXISTS idx_ratings_media ON ratings(media_id);
CREATE INDEX IF NOT EXISTS idx_ratings_user ON ratings(user_id);
CREATE INDEX IF NOT EXISTS idx_favorites_user ON favorites(user_id);
CREATE INDEX IF NOT EXISTS idx_likes_rating ON likes(rating_id);