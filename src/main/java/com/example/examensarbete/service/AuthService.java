package com.example.examensarbete.service;

import com.example.examensarbete.dto.UserDto;
import com.example.examensarbete.entities.Role;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.exception.UserAlreadyExistException;
import com.example.examensarbete.repository.RoleRepository;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.security.jwt.JWTUtil;
import com.example.examensarbete.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDtoMapper userDtoMapper;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Value("${ADMIN_EMAIL}")
    private String adminMail;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDtoMapper userDtoMapper,
                       JWTUtil jwtUtil,
                       UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.userDtoMapper = userDtoMapper;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            logger.info("Attempting login for user with email: {}", request.email());
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            User principal = (User) authentication.getPrincipal();

            UserDto userDto = userDtoMapper.apply(principal);
            String token = jwtUtil.issueToken(userDto.email(), userDto.roles());
            logger.info("Login successful for user: {}", request.email());

            return new AuthenticationResponse(token, userDto);
        } catch (AuthenticationException e) {
            logger.error("Login failed for user: {}", request.email(), e);
        }
        return new AuthenticationResponse(null, null);

    }

    public RegisterResponse register(RegisterRequest request) {
        try {
            logger.info("Attempting registration for user with email: {}", request.email());
            var userCheck = userRepository.findByEmail(request.email());

            if (userCheck.isPresent()) {
                throw new UserAlreadyExistException(userCheck.get().getEmail());
            } else {
                User user = new User();
                user.setEmail(request.email());
                user.setFirstName(request.firstName());
                user.setLastName(request.lastName());
                user.setPassword(passwordEncoder.encode(request.password()));
                Role role = roleRepository.findByName("USER");
                Set<Role> roles = new HashSet<>();
                roles.add(role);

                if (request.email().equals(adminMail)) {
                    role = roleRepository.findByName("ADMIN");
                    roles.add(role);
                }
                user.setRoles(roles);
                userRepository.save(user);
                logger.info("Registration successful for user: {}", request.email());

                return new RegisterResponse(request.email(), request.firstName(), request.lastName());
            }
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", request.email(), e);
            throw e;
        }
    }
}
