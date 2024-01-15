package com.example.examensarbete.controller;

import com.example.examensarbete.dto.UserDto;
import com.example.examensarbete.service.UserService;
import com.example.examensarbete.entities.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /*
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id){
        return userService.getUserById(id);
    }

    @PostMapping
    public User addUser(@RequestBody @Validated UserDto user){
        return userService.addUser(user);
    }

    @PatchMapping("/{id}")
    public User editUser(@PathVariable int id, @RequestBody @Validated UserDto user){
        return userService.editUser(id, user);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return "User with id: " + " was deleted.";
    }

     */

}
