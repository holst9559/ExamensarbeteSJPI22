package com.example.examensarbete.service;

import com.example.examensarbete.dto.CreateUserDto;
import com.example.examensarbete.dto.UserDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.Role;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    /*
    @Transactional
    public User addUser(@Validated CreateUserDto createUserDto){
        var userCheck = userRepository.findByEmail(createUserDto.email());
        if (userCheck.isEmpty()) {
            User user = new User();
            user.setFirstName(createUserDto.firstName());
            user.setLastName(createUserDto.lastName());

            user.setPassword(passwordEncoder.encode(createUserDto.password()));

            user.setEmail(createUserDto.email());
            user.setRole(new Role("USER"));
            return userRepository.save(user);
        }
        throw new IllegalArgumentException("Email is already registered.");
    }

     */



}
