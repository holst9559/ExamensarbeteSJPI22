package com.example.examensarbete.controller;

import com.example.examensarbete.service.UserService;
import com.example.examensarbete.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('OIDC_ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{id:\\d+}")
    public User getUserById(@PathVariable Long id) throws AccessDeniedException {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAuthority('OIDC_ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(403).build();
    }

}
