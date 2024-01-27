package com.example.examensarbete.service;

import com.example.examensarbete.dto.UserDto;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.MissingUserAttributeException;
import com.example.examensarbete.security.jwt.JWTUtil;
import com.example.examensarbete.utils.AuthenticationRequest;
import com.example.examensarbete.utils.AuthenticationResponse;
import com.example.examensarbete.utils.UserDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDtoMapper userDtoMapper;
    private final JWTUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDtoMapper userDtoMapper,
                       JWTUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.userDtoMapper = userDtoMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        User principal = (User) authentication.getPrincipal();
        UserDto userDto = userDtoMapper.apply(principal);
        String token = jwtUtil.issueToken(userDto.email(), userDto.roles());
        return new AuthenticationResponse(token, userDto);
    }

}
