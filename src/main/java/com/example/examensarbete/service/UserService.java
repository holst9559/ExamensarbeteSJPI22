package com.example.examensarbete.service;

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

    public User getUserById(Integer id) throws AuthorizationException {
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
    public void deleteUser(Integer id) throws AuthorizationException {
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

    public boolean isUserAuthorized(Integer id) {
        var userCheck = userRepository.findById(id);
        String userEmail = authenticationFacade.getEmail();
        Set<String> userRoles = authenticationFacade.getRoles();

        return userRoles.contains("ROLE_ADMIN") || userCheck.isPresent() && userEmail.equals(userCheck.get().getEmail());
    }
    /*

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


     */
}
