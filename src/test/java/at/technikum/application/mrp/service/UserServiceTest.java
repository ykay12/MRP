package at.technikum.application.mrp.service;

import at.technikum.application.mrp.exception.UnauthorizedException;
import at.technikum.application.mrp.exception.UserAlreadyExistsException;
import at.technikum.application.mrp.model.User;
import at.technikum.application.mrp.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    void givenNewUser_whenRegister_thenUserCreated() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        User result = userService.register("testuser", "password123");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertNotNull(result.getId());
        assertEquals(0, result.getTotalRatings());
        assertEquals(0.0, result.getAverageScore());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingUsername_whenRegister_thenThrowsException() {
        // Given
        User existingUser = new User("1", "testuser", "pass");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.register("testuser", "password123");
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void givenValidCredentials_whenLogin_thenReturnsToken() {
        // Given
        User user = new User("1", "testuser", "password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String token = userService.login("testuser", "password123");

        // Then
        assertNotNull(token);
        assertEquals("testuser-mrpToken", token);
        verify(userRepository, times(1)).update(any(User.class));
    }

    @Test
    void givenNonExistentUser_whenLogin_thenThrowsUnauthorizedException() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.login("nonexistent", "password");
        });
    }

    @Test
    void givenWrongPassword_whenLogin_thenThrowsUnauthorizedException() {
        // Given
        User user = new User("1", "testuser", "correctpass");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.login("testuser", "wrongpass");
        });
    }

    @Test
    void givenValidToken_whenGetUserByToken_thenReturnsUser() {
        // Given
        User user = new User("1", "testuser", "password");
        user.setToken("valid-token");
        when(userRepository.findByToken("valid-token")).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserByToken("valid-token");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("valid-token", result.getToken());
    }

    @Test
    void givenInvalidToken_whenGetUserByToken_thenThrowsException() {
        // Given
        when(userRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UnauthorizedException.class, () -> {
            userService.getUserByToken("invalid-token");
        });
    }

    @Test
    void givenExistingUserId_whenGetUserById_thenReturnsUser() {
        // Given
        User user = new User("1", "testuser", "password");
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserById("1");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void givenNonExistentUserId_whenGetUserById_thenThrowsException() {
        // Given
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            userService.getUserById("999");
        });
    }

    @Test
    void givenExistingUsername_whenGetUserByUsername_thenReturnsUser() {
        // Given
        User user = new User("1", "testuser", "password");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void whenGetLeaderboard_thenReturnsTopUsers() {
        // Given
        User user1 = new User("1", "user1", "pass");
        user1.setTotalRatings(100);
        User user2 = new User("2", "user2", "pass");
        user2.setTotalRatings(50);
        List<User> topUsers = Arrays.asList(user1, user2);
        when(userRepository.findTopByRatingCount(10)).thenReturn(topUsers);

        // When
        List<User> result = userService.getLeaderboard(10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100, result.get(0).getTotalRatings());
        assertEquals(50, result.get(1).getTotalRatings());
    }
}