package com.example.examensarbete;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.AuthorizationException;
import com.example.examensarbete.exception.UserNotFoundException;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.UserService;
import com.example.examensarbete.utils.AuthenticationFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationFacade authenticationFacade;

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
    void testGetUserById_AdminRole_Success() throws  AuthorizationException {
        // Arrange
        Long userId = 1L;
        User mockUser = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(authenticationFacade.getEmail()).thenReturn("anton@example.com");
        when(authenticationFacade.getRoles()).thenReturn(Set.of("ROLE_ADMIN"));
        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);

        // Verify that findById and other methods were called as expected
        verify(userRepository, times(2)).findById(userId);
        verify(authenticationFacade, times(1)).getEmail();
        verify(authenticationFacade, times(1)).getRoles();
    }

    @Test
    void testGetUserById_NonAdminRole_Success() throws AuthorizationException {
        // Arrange
        Long userId = 1L;
        User mockUser = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(authenticationFacade.getEmail()).thenReturn("anton@example.com");
        when(authenticationFacade.getRoles()).thenReturn(Set.of("ROLE_USER"));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);

        // Verify that findById and other methods were called as expected
        verify(userRepository, times(2)).findById(userId);
        verify(authenticationFacade, times(1)).getEmail();
        verify(authenticationFacade, times(1)).getRoles();
    }

    @Test
    void testGetUserById_UserNotFound_ThrowsException() {
        // Arrange
        Long userId = 3L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        // Verify that findById and other methods were called as expected
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_AccessDenied_ThrowsException() {
        // Arrange
        Long userId = 1L;
        User mockUser = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(authenticationFacade.getEmail()).thenReturn("user@example.com");
        when(authenticationFacade.getRoles()).thenReturn(Set.of("ROLE_USER"));

        // Act and Assert
        assertThrows(AuthorizationException.class, () -> userService.getUserById(userId));

        // Verify that findById and other methods were called as expected
        verify(userRepository, times(2)).findById(userId);
        verify(authenticationFacade, times(1)).getEmail();
        verify(authenticationFacade, times(1)).getRoles();
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
    void deleteUser_UserFoundAndDeleted() {
        // Mocking Repository Behavior
        Long userId = 1L;
        User userToDelete = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        when(authenticationFacade.getRoles()).thenReturn(Set.of("ROLE_ADMIN"));

        // Method Invocation and Assertion
        Assertions.assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository, times(2)).findById(userId);
        verify(userRepository, times(1)).delete(userToDelete);
        verify(authenticationFacade, times(1)).getEmail();
        verify(authenticationFacade, times(1)).getRoles();
    }

    @Test
    void deleteUser_AccessDenied_ThrowsException() {
        // Mocking Repository Behavior
        Long userId = 1L;
        User userToDelete = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDelete));
        when(authenticationFacade.getEmail()).thenReturn("user@example.com");
        when(authenticationFacade.getRoles()).thenReturn(Set.of("ROLE_USER"));

        // Method Invocation and Assertion
        assertThrows(AuthorizationException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(2)).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
        verify(authenticationFacade, times(1)).getEmail();
        verify(authenticationFacade, times(1)).getRoles();
    }

    @Test
    void deleteUser_UserNotFound() {
        // Mocking Repository Behavior
        Long userId = 1L;
        User userToDelete = createUser(1L, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
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