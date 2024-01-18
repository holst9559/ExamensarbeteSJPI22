package com.example.examensarbete.controller;

import com.example.examensarbete.dto.CreateUserDto;
import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.dto.UserDto;
import com.example.examensarbete.service.UserService;
import com.example.examensarbete.entities.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id){
        return userService.getUserById(id);
    }


    /*

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return "User with id: " + " was deleted.";
    }
    */

}
