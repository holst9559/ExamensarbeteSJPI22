package com.example.examensarbete.controller;

import com.example.examensarbete.utils.AuthenticationRequest;
import com.example.examensarbete.utils.AuthenticationResponse;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import com.example.examensarbete.service.AuthService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) throws IOException {
        System.out.println("TEST");
        System.out.println(request);

        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, response.token())
                .body(response);

    }
}
