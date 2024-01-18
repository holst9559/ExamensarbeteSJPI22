package com.example.examensarbete.service;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.Role;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Transactional
    public void addUser(@Validated GoogleUser googleUser){
        var userCheck = userRepository.findById(googleUser.id());
        if (userCheck.isEmpty()) {
            User user = updateUserMethod(new User(), googleUser);
            userRepository.save(user);
        }
        throw new IllegalArgumentException("Email is already registered.");
    }

    @Transactional
    public void updateUser(@Validated GoogleUser googleUser){
        var userCheck = userRepository.findById(googleUser.id());
        if(userCheck.isPresent()){
            User userToUpdate = updateUserMethod(userCheck.get(), googleUser);
            userRepository.save(userToUpdate);
        }
    }

    private User updateUserMethod(User user, GoogleUser googleUser) {
        user.setId(googleUser.id());
        user.setFirstName(googleUser.givenName());
        user.setLastName(googleUser.familyName());
        user.setFullName(googleUser.fullName());
        user.setEmail(googleUser.email());
        user.setPictureUrl(googleUser.picture());
        return user;
    }




}
