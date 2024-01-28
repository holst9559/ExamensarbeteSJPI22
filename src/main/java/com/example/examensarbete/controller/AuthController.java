package com.example.examensarbete.controller;

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

import java.io.IOException;

@RestController
//@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request, HttpServletResponse response) throws IOException {
        AuthenticationResponse responseObj = authService.login(request);

        Cookie cookie = new Cookie("JWT-TOKEN", responseObj.token());
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7200);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, responseObj.token())
                .body(responseObj);

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws IOException{
        System.out.println(request);

        RegisterResponse response = authService.register(request);

        return ResponseEntity.ok()
                .body(response);

    }
}
