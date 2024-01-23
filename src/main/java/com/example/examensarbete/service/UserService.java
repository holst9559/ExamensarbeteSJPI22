package com.example.examensarbete.service;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.AuthorizationException;
import com.example.examensarbete.exception.UserNotFoundException;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.utils.AuthenticationFacade;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
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

    public User getUserById(Long id) throws AuthorizationException {
        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: '{}", id);
                    return new UserNotFoundException(id);
                });

        if (isUserAuthorized(id)) {
            logger.info("User was returned successfully");
            return user;
        } else {
            logger.error("Access denied to fetch user with id: '{}'", id);
            throw new AuthorizationException();
        }
    }

    @Transactional
    public void addUser(@Validated GoogleUser googleUser) {
        var userCheck = userRepository.findByEmail(googleUser.email());
        if (userCheck.isEmpty()) {
            User user = updateUserMethod(new User(), googleUser);
            userRepository.save(user);
        } else {
            logger.error("Email is already registered with email: '{}'", googleUser.email());
            throw new IllegalArgumentException("Email is already registered.");
        }
    }

    @Transactional
    public void updateUser(@Validated GoogleUser googleUser) {
        userRepository.findByEmail(googleUser.email())
                .ifPresentOrElse(
                        existingUser -> {
                            User userToUpdate = updateUserMethod(existingUser, googleUser);
                            try {
                                userRepository.save(userToUpdate);
                                logger.info("User with email: '{}' was updated. Details: {}", googleUser.email(), getUpdateDetails(userToUpdate, googleUser));
                            } catch (Exception e) {
                                logger.error("Failed to update user with email: '{}'", googleUser.email(), e);
                            }
                        },
                        () -> logger.warn("User with email: '{}' not found. Update skipped.", googleUser.email())
                );
    }

    @Transactional
    public void deleteUser(Long id) throws AuthorizationException {
        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User not found with id: '{}", id);
            return new UserNotFoundException(id);
        });

        if (isUserAuthorized(id)) {
            try {
                userRepository.delete(user);
                logger.info("User with id: '{}' was deleted", id);
            } catch (Exception e) {
                logger.error("Failed to delete user with id: '{}'", id, e);
                throw new RuntimeException("Failed to delete user.", e);
            }
        } else {
            logger.warn("User deletion unauthorized for user with id: '{}'", id);
            throw new AuthorizationException();
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

    public boolean isUserAuthorized(Long id) {
        var userCheck = userRepository.findById(id);
        String userEmail = authenticationFacade.getEmail();
        Set<String> userRoles = authenticationFacade.getRoles();

        return userRoles.contains("OIDC_ADMIN") || userCheck.isPresent() && userEmail.equals(userCheck.get().getEmail());
    }

    private String getUpdateDetails(User updatedUser, GoogleUser googleUser) {
        StringBuilder details = new StringBuilder("Updated details:\n");

        if (!updatedUser.getFirstName().equals(googleUser.givenName())) {
            details.append("First Name: ").append(googleUser.givenName()).append("\n");
        }

        if (!updatedUser.getLastName().equals(googleUser.familyName())) {
            details.append("Last Name: ").append(googleUser.familyName()).append("\n");
        }

        return details.toString();
    }

}
