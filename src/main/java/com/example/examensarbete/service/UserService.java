package com.example.examensarbete.service;

import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.AuthorizationException;
import com.example.examensarbete.exception.UserNotFoundException;
import com.example.examensarbete.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    @Value("${JWT_SECRET}")
    private String SECRET_KEY;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public User getUserById(Integer id, HttpServletRequest request) throws AuthorizationException {
        var user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: '{}", id);
                    return new UserNotFoundException(id);
                });

        if (isUserAuthorized(id, request)) {
            logger.info("User was returned successfully");
            return user;
        } else {
            logger.error("Access denied to fetch user with id: '{}'", id);
            throw new AuthorizationException();
        }
    }

    @Transactional
    public void deleteUser(Integer id, HttpServletRequest request) throws AuthorizationException {
        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User not found with id: '{}", id);
            return new UserNotFoundException(id);
        });

        if (isUserAuthorized(id, request)) {
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

    public boolean isUserAuthorized(Integer id, HttpServletRequest request) {
        var userCheck = userRepository.findById(id);
        Map<String, Object> userDetails = extractUserDetailsFromToken(request);
        String email = (String) userDetails.get("email");
        List<String> roles = (List<String>) userDetails.get("roles");

        return roles.contains("ROLE_ADMIN") || userCheck.isPresent() && email.equals(userCheck.get().getEmail());
    }

    private Map<String, Object> extractUserDetailsFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("email", claims.getSubject());
            userDetails.put("roles", claims.get("scopes", List.class));

            return userDetails;
        }
        return new HashMap<>();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}