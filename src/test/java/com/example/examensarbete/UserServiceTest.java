package com.example.examensarbete;

import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.AuthorizationException;
import com.example.examensarbete.exception.UserNotFoundException;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Value("${JWT_SECRET}")
    private String SECRET_KEY;



    @Test
    void getAllUsers() {
        // Mocking Repository Behavior
        when(userRepository.findAll()).thenReturn(List.of(
                createUser(1, "anton@example.com", "Anton", "Holst"),
                createUser(2, "elin@example.com", "Elin", "Holst")
        ));

        // Method Invocation and Assertion
        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }
/*
    @Test
    void testGetUserById_AdminRole_Success() throws AuthorizationException {
        // Create a mock HttpServletRequest
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + generateMockJwt("testUser", "ROLE_USER"));
        System.out.println(generateMockJwt("testUser", "ROLE_USER"));

        // Arrange
        Integer userId = 1;
        User mockUser = createUser(1, "anton@example.com", "Anton", "Holst");

        // Mock the behavior of extractUserDetailsFromToken
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("email", "anton@example.com");
        userDetails.put("roles", Set.of("ROLE_ADMIN"));
        when(extractUserDetailsFromToken(mockRequest)).thenReturn(userDetails);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        User result = userService.getUserById(userId, mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result);

        // Verify that findById and other methods were called as expected
        verify(userRepository, times(1)).findById(userId);
    }



    @Test
    void testGetUserById_NonAdminRole_Success() throws AuthorizationException {
        // Arrange
        Integer userId = 1;
        User mockUser = createUser(1, "anton@example.com", "Anton", "Holst");
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
        Integer userId = 3;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        // Verify that findById and other methods were called as expected
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_AccessDenied_ThrowsException() {
        // Arrange
        Integer userId = 1;
        User mockUser = createUser(1, "anton@example.com", "Anton", "Holst");
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
        Integer userId = 1;
        User userToDelete = createUser(1, "anton@example.com", "Anton", "Holst");
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
        Integer userId = 1;
        User userToDelete = createUser(1, "anton@example.com", "Anton", "Holst");
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
        Integer userId = 1;
        User userToDelete = createUser(1, "anton@example.com", "Anton", "Holst");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(anyInt());
    }
*/

    // Helper methods
    private User createUser(Integer id, String email, String first, String last) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(first);
        user.setLastName(last);
        return user;
    }

    private Map<String, Object> extractUserDetailsFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("email", claims.getSubject());
            userDetails.put("roles", claims.get("scopes", List.class));

            return userDetails;
        }
        return new HashMap<>();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor("mysecretkey1wadwabduiagd81982gd92d179dv21uidwajkdbnw319d7162d97127ydt27198d5621f9d761g9f7r163f138276f1028936fa282073h6f8a7263f8a7623fh8a2763fa289f3123".getBytes());
    }

    private static String generateMockJwt(String subject, String... roles) {
        // Set the expiration time to 1 hour from now
        long expirationMillis = System.currentTimeMillis() + 3600000;

        // Build JWT claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", subject);
        claims.put("exp", new Date(expirationMillis));
        claims.put("roles", Arrays.asList(roles));

        // Generate JWT
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, "mysecretkey1wadwabduiagd81982gd92d179dv21uidwajkdbnw319d7162d97127ydt27198d5621f9d761g9f7r163f138276f1028936fa282073h6f8a7263f8a7623fh8a2763fa289f3123") // Replace with your secret key
                .compact();
    }


}