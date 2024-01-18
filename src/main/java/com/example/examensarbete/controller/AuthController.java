package com.example.examensarbete.controller;


import com.example.examensarbete.dto.GoogleUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.example.examensarbete.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUserData(@AuthenticationPrincipal OAuth2User principal){
        try{
            Optional<GoogleUser> googleUser = Optional.ofNullable(authService.getUserData(principal));

            return googleUser.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user data found"));
        }catch (Exception e){
            logger.error("Error retrieving data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving data");
        }
    }


}
