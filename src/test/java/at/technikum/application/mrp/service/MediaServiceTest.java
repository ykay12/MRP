package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.ForbiddenException;
import at.technikum.application.mrp.model.Media;
import at.technikum.application.mrp.model.MediaType;
import at.technikum.application.mrp.repository.MediaRepository;
import at.technikum.application.todo.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    private MediaService mediaService;

    @BeforeEach
    void setUp() {
        mediaService = new MediaService(mediaRepository);
    }

    @Test
    void givenNewMedia_whenCreate_thenMediaCreated() {
        // Given
        Media media = new Media(null, "Test Movie", "Description", MediaType.MOVIE, 2024,
                Arrays.asList("Action", "Drama"), 16, null);
        when(mediaRepository.save(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Media result = mediaService.createMedia(media, "creator-123");

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("creator-123", result.getCreatorId());
        assertEquals(0.0, result.getAverageRating());
        assertEquals(0, result.getRatingCount());
        verify(mediaRepository, times(1)).save(any(Media.class));
    }

    @Test
    void givenExistingMediaId_whenGetMediaById_thenReturnsMedia() {
        // Given
        Media media = new Media("1", "Test Movie", "Description", MediaType.MOVIE, 2024,
                Arrays.asList("Action"), 16, "creator-123");
        when(mediaRepository.findById("1")).thenReturn(Optional.of(media));

        // When
        Media result = mediaService.getMediaById("1");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Movie", result.getTitle());
    }

    @Test
    void givenNonExistentMediaId_whenGetMediaById_thenThrowsException() {
        // Given
        when(mediaRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            mediaService.getMediaById("999");
        });
    }

    @Test
    void givenOwner_whenUpdateMedia_thenMediaUpdated() {
        // Given
        Media existingMedia = new Media("1", "Old Title", "Old Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 12, "creator-123");
        Media updatedMedia = new Media(null, "New Title", "New Desc", MediaType.SERIES, 2024,
                Arrays.asList("Drama"), 16, null);

        when(mediaRepository.findById("1")).thenReturn(Optional.of(existingMedia));
        when(mediaRepository.update(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Media result = mediaService.updateMedia("1", updatedMedia, "creator-123");

        // Then
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Desc", result.getDescription());
        assertEquals(MediaType.SERIES, result.getMediaType());
        verify(mediaRepository, times(1)).update(any(Media.class));
    }

    @Test
    void givenNonOwner_whenUpdateMedia_thenThrowsForbiddenException() {
        // Given
        Media existingMedia = new Media("1", "Title", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 12, "creator-123");
        Media updatedMedia = new Media(null, "New Title", "New Desc", MediaType.MOVIE, 2024,
                Arrays.asList("Drama"), 16, null);

        when(mediaRepository.findById("1")).thenReturn(Optional.of(existingMedia));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            mediaService.updateMedia("1", updatedMedia, "different-user");
        });
        verify(mediaRepository, never()).update(any(Media.class));
    }

    @Test
    void givenOwner_whenDeleteMedia_thenMediaDeleted() {
        // Given
        Media media = new Media("1", "Title", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 12, "creator-123");
        when(mediaRepository.findById("1")).thenReturn(Optional.of(media));

        // When
        mediaService.deleteMedia("1", "creator-123");

        // Then
        verify(mediaRepository, times(1)).delete("1");
    }

    @Test
    void givenNonOwner_whenDeleteMedia_thenThrowsForbiddenException() {
        // Given
        Media media = new Media("1", "Title", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 12, "creator-123");
        when(mediaRepository.findById("1")).thenReturn(Optional.of(media));

        // When & Then
        assertThrows(ForbiddenException.class, () -> {
            mediaService.deleteMedia("1", "different-user");
        });
        verify(mediaRepository, never()).delete(anyString());
    }

    @Test
    void givenTitle_whenSearchByTitle_thenReturnsMatchingMedia() {
        // Given
        Media media1 = new Media("1", "Inception", "Desc", MediaType.MOVIE, 2010,
                Arrays.asList("Sci-Fi"), 12, "creator");
        Media media2 = new Media("2", "Interstellar", "Desc", MediaType.MOVIE, 2014,
                Arrays.asList("Sci-Fi"), 12, "creator");
        when(mediaRepository.findByTitle("in")).thenReturn(Arrays.asList(media1, media2));

        // When
        List<Media> result = mediaService.searchByTitle("in");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void givenGenreFilter_whenFilterMedia_thenReturnsFilteredMedia() {
        // Given
        Media media1 = new Media("1", "Action Movie", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 16, "creator");
        Media media2 = new Media("2", "Drama Movie", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Drama"), 12, "creator");
        Media media3 = new Media("3", "Action Series", "Desc", MediaType.SERIES, 2020,
                Arrays.asList("Action"), 16, "creator");

        when(mediaRepository.findAll()).thenReturn(Arrays.asList(media1, media2, media3));

        // When
        List<Media> result = mediaService.filterMedia("Action", null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getGenres().contains("Action")));
    }

    @Test
    void givenMediaTypeFilter_whenFilterMedia_thenReturnsOnlyMovies() {
        // Given
        Media media1 = new Media("1", "Movie 1", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 16, "creator");
        Media media2 = new Media("2", "Series 1", "Desc", MediaType.SERIES, 2020,
                Arrays.asList("Drama"), 12, "creator");

        when(mediaRepository.findAll()).thenReturn(Arrays.asList(media1, media2));

        // When
        List<Media> result = mediaService.filterMedia(null, MediaType.MOVIE, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(MediaType.MOVIE, result.get(0).getMediaType());
    }

    @Test
    void givenSortByRating_whenSortMedia_thenReturnsDescendingByRating() {
        // Given
        Media media1 = new Media("1", "Low Rated", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 16, "creator");
        media1.setAverageRating(3.5);

        Media media2 = new Media("2", "High Rated", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Drama"), 12, "creator");
        media2.setAverageRating(4.8);

        List<Media> mediaList = Arrays.asList(media1, media2);

        // When
        List<Media> result = mediaService.sortMedia(mediaList, "rating");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(4.8, result.get(0).getAverageRating());
        assertEquals(3.5, result.get(1).getAverageRating());
    }

    @Test
    void whenUpdateAverageRating_thenMediaIsUpdated() {
        // Given
        Media media = new Media("1", "Title", "Desc", MediaType.MOVIE, 2020,
                Arrays.asList("Action"), 16, "creator");
        when(mediaRepository.findById("1")).thenReturn(Optional.of(media));
        when(mediaRepository.update(any(Media.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        mediaService.updateAverageRating("1", 4.5, 10);

        // Then
        verify(mediaRepository, times(1)).update(any(Media.class));
    }
}