package com.example.examensarbete.controller;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.MissingUserAttributeException;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.security.JwtTokenService;
import com.example.examensarbete.service.UserService;
import com.nimbusds.oauth2.sdk.Request;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.example.examensarbete.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final JwtTokenService jwtTokenService;

    private final UserRepository userRepository;

    private final UserService userService;

    public AuthController(AuthService authService,
                          JwtTokenService jwtTokenService,
                          UserRepository userRepository,
                          UserService userService){
        this.authService = authService;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @GetMapping("/login")
    public ResponseEntity<String> loginTest(@RequestHeader Map<String, String> requestHeaders){
        System.out.println(requestHeaders);

        System.out.println("TEST");
        return ResponseEntity.ok().body("TEST");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody OAuth2User requestBody) throws IOException {
        System.out.println("TEST");
        System.out.println(requestBody);
//        User user = extractUserDetails(requestBody);
//        String jwtToken = jwtTokenService.generateToken(user);
//
//        Cookie cookie = new Cookie("JWT-TOKEN", jwtToken);
//        cookie.setHttpOnly(true);

        return ResponseEntity.ok().body("TEST");

    }

    private User extractUserDetails(OAuth2User oauth2User) {
        if (oauth2User instanceof DefaultOAuth2User) {
            DefaultOAuth2User auth = (DefaultOAuth2User) oauth2User;
            GoogleUser googleUser = authService.getUserData(auth);

            var userCheck = userRepository.findByEmail(googleUser.email());
            if (userCheck.isEmpty()) {
                logger.info("New user detected, {}", googleUser.fullName());
                userService.addUser(googleUser);
            } else {
                logger.info("Updating existing user, {}", googleUser.fullName());
                userService.updateUser(googleUser);
            }

            return new User(googleUser.givenName(), googleUser.familyName(), googleUser.fullName(), googleUser.email());
        }

        throw new MissingUserAttributeException("Unsupported OAuth2User type");
    }

    /*
    @GetMapping("/user")
    public ResponseEntity<Object> getUserData(@AuthenticationPrincipal OAuth2User principal){
        try{
            Optional<GoogleUser> googleUser = Optional.ofNullable(authService.getUserData(principal));

            return googleUser.<ResponseEntity<Object>>map(ResponseEntity::ok).orElseGet(() ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user data found"));
        }catch (Exception e){
            logger.error("Error retrieving data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving data");
        }
    }

     */
}
