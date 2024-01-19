package com.example.examensarbete.service;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @Transactional
    public void addUser(@Validated GoogleUser googleUser) {
        var userCheck = userRepository.findByEmail(googleUser.email());
        if (userCheck.isEmpty()) {
            User user = updateUserMethod(new User(), googleUser);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("Email is already registered.");
        }
    }

    @Transactional
    public void updateUser(@Validated GoogleUser googleUser) {
        var userCheck = userRepository.findByEmail(googleUser.email());
        if (userCheck.isPresent()) {
            User userToUpdate = updateUserMethod(userCheck.get(), googleUser);
            userRepository.save(userToUpdate);
        }
    }

    @Transactional
    public void checkAndDeleteUser(Long id, GoogleUser googleUser) {
        checkPermission(googleUser);
        var userCheck = userRepository.findById(id);

        if (userCheck.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User with the ID: " + id + " was not found.");
        }
    }

    //Replace with annotations once I get ROLES_ to work
    private void checkPermission(GoogleUser googleUser) {
        var adminCheck = userRepository.findByEmail(googleUser.email())
                .orElseThrow(() -> new RuntimeException("Admin user not found"));

        if (!adminCheck.getEmail().equals(adminEmail)) {
            throw new RuntimeException("No permission to delete user");
        }
    }

    private User updateUserMethod(User user, GoogleUser googleUser) {
        user.setFirstName(googleUser.givenName());
        user.setLastName(googleUser.familyName());
        user.setFullName(googleUser.fullName());
        user.setEmail(googleUser.email());
        user.setPictureUrl(googleUser.picture());
        return user;
    }

}
