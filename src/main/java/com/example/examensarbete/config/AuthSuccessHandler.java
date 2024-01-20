package com.example.examensarbete.config;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.AuthService;
import com.example.examensarbete.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(AuthSuccessHandler.class);

    private final AuthService authService;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public AuthSuccessHandler(AuthService authService,
                              UserService userService,
                              UserRepository userRepository) {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication authentication) throws IOException {
        String redirectUrl = null;
        logger.debug("Authentication successful for user: , {}", authentication.getName());
        if (authentication.getPrincipal() instanceof DefaultOAuth2User auth) {
            GoogleUser googleUser = authService.getUserData(auth);
            var userCheck = userRepository.findByEmail(googleUser.email());
            if (userCheck.isEmpty()) {
                logger.info("New user detected, {}", googleUser.fullName());
                userService.addUser(googleUser);
            } else {
                logger.info("Updating existing user, {}", googleUser.fullName());
                userService.updateUser(googleUser);
            }
        }
        redirectUrl = "/user";
        new DefaultRedirectStrategy().sendRedirect(req, res, redirectUrl);
    }
}

