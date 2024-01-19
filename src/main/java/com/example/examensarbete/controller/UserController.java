package com.example.examensarbete.controller;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.service.AuthService;
import com.example.examensarbete.service.UserService;
import com.example.examensarbete.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService,
                          AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        if(authentication.getPrincipal() instanceof DefaultOAuth2User auth){
            GoogleUser googleUser = authService.getUserData(auth);
            userService.deleteUser(id, googleUser);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(403).build();
    }

}
