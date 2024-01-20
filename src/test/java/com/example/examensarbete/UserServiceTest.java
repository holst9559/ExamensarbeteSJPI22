package com.example.examensarbete;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers() {
        // Mocking Repository Behavior
        when(userRepository.findAll()).thenReturn(List.of(
                createUser(1L, "anton@example.com", "Anton", "Holst"),
                createUser(2L, "elin@example.com", "Elin", "Holst")
        ));

        // Method Invocation and Assertion
        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void getUserById_UserFound() {
        // Mocking Repository Behavior
        Long userId = 1L;
        User user = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Method Invocation and Assertion
        User result = userService.getUserById(userId);
        assertEquals(user, result);
    }

    @Test
    void getUserById_UserNotFound() {
        // Mocking Repository Behavior
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
    }

    @Test
    void addUser_UserDoesNotExist() {
        // Mocking Repository Behavior
        GoogleUser googleUser = createGoogleUser();
        when(userRepository.findByEmail(googleUser.email())).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertDoesNotThrow(() -> userService.addUser(googleUser));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addUser_UserAlreadyExists() {
        // Mocking Repository Behavior
        GoogleUser googleUser = createGoogleUser();
        when(userRepository.findByEmail(googleUser.email())).thenReturn(Optional.of(createUser(1L, googleUser.email(), googleUser.givenName(), googleUser.familyName())));

        // Method Invocation and Assertion
        assertThrows(IllegalArgumentException.class, () -> userService.addUser(googleUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_UserExists() {
        // Mocking Repository Behavior
        GoogleUser googleUser = createGoogleUser();
        User existingUser = createUser(1L, googleUser.email(), googleUser.givenName(), googleUser.familyName());
        when(userRepository.findByEmail(googleUser.email())).thenReturn(Optional.of(existingUser));

        // Method Invocation and Assertion
        assertDoesNotThrow(() -> userService.updateUser(googleUser));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_UserDoesNotExist() {
        // Mocking Repository Behavior
        GoogleUser googleUser = createGoogleUser();
        when(userRepository.findByEmail(googleUser.email())).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertDoesNotThrow(() -> userService.updateUser(googleUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_UserFoundAnd() {
        // Mocking Repository Behavior
        Long userId = 1L;
        User userToDelete = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));

        // Method Invocation and Assertion
        Assertions.assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_UserNotFound() {
        // Mocking Repository Behavior
        Long userId = 1L;
        User userToDelete = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(anyLong());
    }

    // Helper methods
    private User createUser(Long id, String email, String first, String last) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(first);
        user.setLastName(last);
        user.setFullName(first + " " + last);
        return user;
    }

    private GoogleUser createGoogleUser() {
        return new GoogleUser("Anton", "Holst", "Anton" + " " + "Holst", "anton@example.com", "https://example.com/picture");
    }

}
