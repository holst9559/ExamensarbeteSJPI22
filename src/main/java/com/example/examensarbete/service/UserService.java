package com.example.examensarbete.service;

import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    public User getUserById(@Validated Long id){
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }


}
