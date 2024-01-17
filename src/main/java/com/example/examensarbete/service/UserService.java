package com.example.examensarbete.service;

import com.example.examensarbete.entities.Role;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Transactional
    public User addUser(@Validated Principal principal){
        var userCheck = userRepository.findByEmail(principal.getName());
        if (userCheck.isEmpty()) {
            User user = new User();
            user.setRole(new Role("USER"));
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Email is already registered.");
    }




}
