package com.example.examensarbete;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.exception.InvalidUserTypeException;
import com.example.examensarbete.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private OAuth2User mockPrincipal;

    @InjectMocks
    private AuthService authService;

    @Test
    void testGetUserData() {
        // Mock OAuth2User attributes
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("given_name", "John");
        attributes.put("family_name", "Doe");
        attributes.put("name", "John Doe");
        attributes.put("email", "john.doe@example.com");
        attributes.put("picture", "https://example.com/picture.jpg");

        // Configure the mockPrincipal to return the mocked attributes
        when(mockPrincipal.getAttributes()).thenReturn(attributes);

        // Call the method under test
        GoogleUser googleUser = authService.getUserData(mockPrincipal);

        // Verify the results
        assertEquals("John", googleUser.givenName());
        assertEquals("Doe", googleUser.familyName());
        assertEquals("John Doe", googleUser.fullName());
        assertEquals("john.doe@example.com", googleUser.email());
        assertEquals("https://example.com/picture.jpg", googleUser.picture());
    }

    @Test
    void testGetUserDataWithMissingAttribute() {
        // Mock OAuth2User attributes with a missing attribute ("family_name")
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("given_name", "John");
        attributes.put("name", "John Doe");
        attributes.put("email", "john.doe@example.com");
        attributes.put("picture", "https://example.com/picture.jpg");

        // Configure the mockPrincipal to return the mocked attributes
        when(mockPrincipal.getAttributes()).thenReturn(attributes);

        // Call the method under test
        GoogleUser googleUser = authService.getUserData(mockPrincipal);

        // Verify that the missing attribute does not cause an issue
        assertEquals("John", googleUser.givenName());
        // The familyName should be null since it's missing
        assertEquals("John Doe", googleUser.fullName());
        assertEquals("john.doe@example.com", googleUser.email());
        assertEquals("https://example.com/picture.jpg", googleUser.picture());
        assertThrows(InvalidUserTypeException.class, () -> authService.getUserData(mockPrincipal));
    }
}
