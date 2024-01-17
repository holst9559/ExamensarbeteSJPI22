package com.example.examensarbete.config;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    private static final Logger logger = Logger.getLogger(AuthSuccessHandler.class);

    private final AuthService authService;

    @Autowired
    public AuthSuccessHandler(AuthService authService){
        this.authService = authService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication authentication) throws IOException{
        logger.info("Authentication successful for user: " + authentication.getName());

        if(authentication.getPrincipal() instanceof OAuth2User){
            OAuth2User auth = (OAuth2User) authentication.getPrincipal();
            GoogleUser googleUser = authService.getUserData(auth);
            authService.updateUser(googleUser);
        }

        res.sendRedirect("/");
    }
}

