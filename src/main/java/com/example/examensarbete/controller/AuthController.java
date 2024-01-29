package com.example.examensarbete.controller;

import com.example.examensarbete.exception.AuthorizationException;
import com.example.examensarbete.utils.AuthenticationRequest;
import com.example.examensarbete.utils.AuthenticationResponse;
import com.example.examensarbete.utils.RegisterRequest;
import com.example.examensarbete.utils.RegisterResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import com.example.examensarbete.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) {
        logger.info("Received login request for user: {}", request.email());
        AuthenticationResponse responseObj = authService.login(request);

        logger.info("Login successful for user: {}", request.email());

        Cookie cookie = new Cookie("JWT-TOKEN", responseObj.token());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7200);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);

        logger.info("JWT token added to cookie for user: {}", request.email());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, responseObj.token())
                .body(responseObj);

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        logger.info("Received registration request for user: {}", request.email());

        RegisterResponse response = authService.register(request);
        logger.info("Registration successful for user: {}", request.email());
        return ResponseEntity.ok()
                .body(response);

    }
}
