# Media Rating Platform (MRP) - Project Protocol

**Student Name:** Yusuf    
**Date:** January 6, 2026  
**Git Repository:** [[MRP Project](https://github.com/ykay12/MRP)]


## 1. Application Design

### Architecture
Layered architecture with 5 layers:
- **HTTP Layer:** Java's HttpServer + custom RequestMapper
- **Application Layer:** Routing, authentication middleware, exception handling
- **Controller Layer:** HTTP request/response handling
- **Service Layer:** Business logic
- **Repository Layer:** Database access via JDBC + PreparedStatements

### Key Design Decisions
1. **Pure HTTP:** Used HttpServer without framework, custom routing
2. **Repository Pattern:** Interface-based repositories for testability
3. **ThreadLocal Context:** Store authenticated user per request without passing through all methods
4. **Token Auth:** Simple `{username}-mrpToken` format with Bearer tokens
5. **Exception Mapping:** Centralized exception-to-HTTP-status translation

### Database Schema
- `users` table: user accounts, stats, tokens
- `media` table: movies/series/games with genres (TEXT[])
- `ratings` table: 1-5 stars, comments, likes (UNIQUE per user+media)
- `favorites` table: user favorites list
- `likes` table: rating likes


## 2. Lessons Learned

### ThreadLocal for Authentication Context
Initially passed user through every method parameter. Switched to ThreadLocal - much cleaner, but must remember to `.clear()` after each request to avoid memory leaks.

### PostgreSQL Arrays
TEXT[] arrays in PostgreSQL require special handling with `conn.createArrayOf()` and casting when reading. More complex than expected but avoids junction tables.

### Pure HTTP is a lot of work
No framework means manually parsing query params, routing, content negotiation. Gained deep understanding of HTTP but appreciated what frameworks will provide in the future.


## 3. Unit Testing Strategy

### Coverage
20 unit tests across 3 service classes (MediaService, RatingService, UserService) covering core business logic.

### Strategy
- **Mock repositories** using Mockito to isolate service layer
- **Test business rules:** validation, authorization, edge cases
- **Positive and negative cases:** both success and failure scenarios

### Test Categories
1. **Creation tests:** Valid input creates objects correctly
2. **Validation tests:** Invalid input (e.g., 0 stars, 6 stars) throws exceptions
3. **Authorization tests:** Non-owners can't modify media/ratings
4. **Business logic tests:** Average rating calculation, duplicate prevention

### Example Test
```java
@Test
void givenStarsTooHigh_whenCreateRating_thenThrowsInvalidRatingException() {
    assertThrows(InvalidRatingException.class, () -> {
        ratingService.createRating("media-1", "user-1", 6, "Comment");
    });
    verify(ratingRepository, never()).save(any(Rating.class));
}
```


## 4. SOLID Principles

### Single Responsibility Principle (SRP)
**Example:** `MediaService` only handles media business logic. Database access delegated to `MediaRepository`. HTTP handling in `MediaController`.

```java
// MediaService: Only business logic
public class MediaService {
    private final MediaRepository mediaRepository;
    
    public Media createMedia(Media media, String creatorId) {
        media.setId(UUID.randomUUID().toString());
        media.setCreatorId(creatorId);
        return mediaRepository.save(media);
    }
}

// MediaRepository: Only database access
public class DbMediaRepository implements MediaRepository {
    public Media save(Media media) {
        // SQL operations only
    }
}
```

**Benefit:** Easy to test services without database, swap implementations.

### Dependency Inversion Principle (DIP)
**Example:** Services depend on repository *interfaces*, not concrete implementations. Can inject different implementations (database, in-memory, mock).

```java
// Service depends on interface
public class MediaService {
    private final MediaRepository mediaRepository; // Interface
    
    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }
}

// Concrete implementations
public interface MediaRepository { ... }
public class DbMediaRepository implements MediaRepository { ... }
public class MemoryMediaRepository implements MediaRepository { ... } // for tests
```

**Benefit:** Easy to mock in tests, could switch databases without changing services.

## 5. Time Tracking

| Task | Time (hours) |
|------|--------------|
| Database schema design | 2 |
| Repository layer implementation | 4 |
| Service layer + business logic | 6 |
| Controller layer + routing | 3 |
| Authentication middleware | 2 |
| Unit tests | 4 |
| Integration testing (curl scripts) | 2 |
| Bug fixes & refinement | 7 |
| **Total** | **30** |