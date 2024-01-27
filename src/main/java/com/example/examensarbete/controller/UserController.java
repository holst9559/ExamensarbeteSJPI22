package com.example.examensarbete.controller;

import com.example.examensarbete.exception.AuthorizationException;
import com.example.examensarbete.service.UserService;
import com.example.examensarbete.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/{id:\\d+}")
    public User getUserById(@PathVariable Integer id) throws AuthorizationException {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) throws AuthorizationException {
        userService.deleteUser(id);
        return ResponseEntity.status(403).build();
    }

}
