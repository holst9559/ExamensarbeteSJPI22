package com.example.examensarbete.service;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.utils.AuthenticationFacade;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationFacade authenticationFacade;

    public UserService(UserRepository userRepository,
                       AuthenticationFacade authenticationFacade) {
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) throws AccessDeniedException {
        var userCheck = userRepository.findById(id);
        String userEmail = authenticationFacade.getEmail();
        Set<String> userRoles = authenticationFacade.getRoles();

        if (userCheck.isPresent()) {
            if ((userRoles.contains("OIDC_ADMIN") || userEmail.equals(userCheck.get().getEmail()))) {
                return userCheck.get();
            }else {
                throw new AccessDeniedException("Access denied");
            }
        } else {
            throw new RuntimeException("User with the id: " + id + " was not found");
        }

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
    public void deleteUser(Long id) {
        var userCheck = userRepository.findById(id);
        if (userCheck.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User with the ID: " + id + " was not found.");
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
