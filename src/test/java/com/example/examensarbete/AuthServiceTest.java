package com.example.examensarbete;

import com.example.examensarbete.controller.AuthController;
import com.example.examensarbete.service.AuthService;
import com.example.examensarbete.utils.AuthenticationRequest;
import com.example.examensarbete.utils.AuthenticationResponse;
import com.example.examensarbete.utils.RegisterRequest;
import com.example.examensarbete.utils.RegisterResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {


    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin_Success() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password");
        AuthenticationResponse mockResponse = new AuthenticationResponse("mockToken", null);

        when(authService.login(request)).thenReturn(mockResponse);

        HttpServletResponse mockResponseObject = mock(HttpServletResponse.class);

        // Act
        ResponseEntity<?> responseEntity = authController.login(request, mockResponseObject);

        // Assert
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(mockResponse, responseEntity.getBody());

        verify(authService, times(1)).login(request);

        // Verify cookie is set
        verify(mockResponseObject, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "John", "Doe", "password");
        RegisterResponse mockResponse = new RegisterResponse("test@example.com", "John", "Doe");

        when(authService.register(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> responseEntity = authController.register(request);

        // Assert
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(mockResponse, responseEntity.getBody());

        verify(authService, times(1)).register(request);
    }
}
