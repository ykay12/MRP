package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.ForbiddenException;
import at.technikum.application.mrp.exception.InvalidRatingException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.MediaType;
import at.technikum.application.mrp.model.Rating;
import at.technikum.application.mrp.repository.RatingRepository;
import at.technikum.application.todo.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MediaService mediaService;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingService = new RatingService(ratingRepository, mediaService);
    }

    @Test
    void givenValidRating_whenCreateRating_thenRatingCreated() {
        // Given
        when(ratingRepository.findByMediaIdAndUserId("media-1", "user-1")).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByMediaId("media-1")).thenReturn(Arrays.asList());

        // When
        Rating result = ratingService.createRating("media-1", "user-1", 5, "Great movie!");

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("media-1", result.getMediaId());
        assertEquals("user-1", result.getUserId());
        assertEquals(5, result.getStars());
        assertEquals("Great movie!", result.getComment());
        assertFalse(result.isConfirmed());
        assertEquals(0, result.getLikes());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void givenStarsTooLow_whenCreateRating_thenThrowsInvalidRatingException() {
        // When & Then
        assertThrows(InvalidRatingException.class, () -> {
            ratingService.createRating("media-1", "user-1", 0, "Comment");
        });
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void givenStarsTooHigh_whenCreateRating_thenThrowsInvalidRatingException() {
        // When & Then
        assertThrows(InvalidRatingException.class, () -> {
            ratingService.createRating("media-1", "user-1", 6, "Comment");
        });
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void givenDuplicateRating_whenCreateRating_thenThrowsInvalidRatingException() {
        // Given
        Rating existingRating = new Rating("1", "media-1", "user-1", 4, "Old comment");
        when(ratingRepository.findByMediaIdAndUserId("media-1", "user-1"))
                .thenReturn(Optional.of(existingRating));

        // When & Then
        assertThrows(InvalidRatingException.class, () -> {
            ratingService.createRating("media-1", "user-1", 5, "New comment");
        });
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    void givenExistingRatingId_whenGetRatingById_thenReturnsRating() {
        // Given
        Rating rating = new Rating("1", "media-1", "user-1", 5, "Great!");
        when(ratingRepository.findById("1")).thenReturn(Optional.of(rating));

        // When
        Rating result = ratingService.getRatingById("1");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals(5, result.getStars());
    }

    @Test
    void givenNonExistentRatingId_whenGetRatingById_thenThrowsException() {
        // Given
        when(ratingRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            ratingService.getRatingById("999");
        });
    }

    @Test
    void givenMediaId_whenGetRatingsByMediaId_thenReturnsRatings() {
        // Given
        Rating rating1 = new Rating("1", "media-1", "user-1", 5, "Great!");
        Rating rating2 = new Rating("2", "media-1", "user-2", 4, "Good!");
        when(ratingRepository.findByMediaId("media-1")).thenReturn(Arrays.asList(rating1, rating2));

        // When
        List<Rating> result = ratingService.getRatingsByMediaId("media-1");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void givenOwner_whenUpdateRating_thenRatingUpdated() {
        // Given
        Rating existingRating = new Rating("1", "media-1", "user-1", 4, "Old comment");
        existingRating.setConfirmed(true);
        when(ratingRepository.findById("1")).thenReturn(Optional.of(existingRating));
        when(ratingRepository.update(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByMediaId("media-1")).thenReturn(Arrays.asList(existingRating));

        // When
        Rating result = ratingService.updateRating("1", "user-1", 5, "Updated comment");

        // Then
        assertNotNull(result);
        assertEquals(5, result.getStars());
        assertEquals("Updated comment", result.getComment());
        assertFalse(result.isConfirmed()); // Should be reset to false after edit
        verify(ratingRepository, times(1)).update(any(Rating.class));
    }

    @Test
    void givenNonOwner_whenUpdateRating_thenThrowsForbiddenException() {
        // Given
        Rating existingRating = new Rating("1", "media-1", "user-1", 4, "Comment");
        when(ratingRepository.findById("1")).thenReturn(Optional.of(existingRating));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            ratingService.updateRating("1", "different-user", 5, "New comment");
        });
        verify(ratingRepository, never()).update(any(Rating.class));
    }

    @Test
    void givenOwner_whenDeleteRating_thenRatingDeleted() {
        // Given
        Rating rating = new Rating("1", "media-1", "user-1", 5, "Comment");
        when(ratingRepository.findById("1")).thenReturn(Optional.of(rating));
        when(ratingRepository.findByMediaId("media-1")).thenReturn(Arrays.asList());

        // When
        ratingService.deleteRating("1", "user-1");

        // Then
        verify(ratingRepository, times(1)).delete("1");
    }

    @Test
    void givenNonOwner_whenDeleteRating_thenThrowsForbiddenException() {
        // Given
        Rating rating = new Rating("1", "media-1", "user-1", 5, "Comment");
        when(ratingRepository.findById("1")).thenReturn(Optional.of(rating));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            ratingService.deleteRating("1", "different-user");
        });
        verify(ratingRepository, never()).delete(anyString());
    }

    @Test
    void givenOwner_whenConfirmRating_thenRatingConfirmed() {
        // Given
        Rating rating = new Rating("1", "media-1", "user-1", 5, "Comment");
        rating.setConfirmed(false);
        when(ratingRepository.findById("1")).thenReturn(Optional.of(rating));
        when(ratingRepository.update(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rating result = ratingService.confirmRating("1", "user-1");

        // Then
        assertNotNull(result);
        assertTrue(result.isConfirmed());
        verify(ratingRepository, times(1)).update(any(Rating.class));
    }

    @Test
    void givenNonOwner_whenConfirmRating_thenThrowsForbiddenException() {
        // Given
        Rating rating = new Rating("1", "media-1", "user-1", 5, "Comment");
        when(ratingRepository.findById("1")).thenReturn(Optional.of(rating));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            ratingService.confirmRating("1", "different-user");
        });
        verify(ratingRepository, never()).update(any(Rating.class));
    }

    @Test
    void whenLikeRating_thenLikesIncremented() {
        // Given
        Rating rating = new Rating("1", "media-1", "user-1", 5, "Comment");
        rating.setLikes(5);
        when(ratingRepository.findById("1")).thenReturn(Optional.of(rating));
        when(ratingRepository.update(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ratingService.likeRating("1");

        // Then
        verify(ratingRepository, times(1)).update(argThat(r -> r.getLikes() == 6));
    }
}