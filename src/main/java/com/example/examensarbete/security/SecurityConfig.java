package com.example.examensarbete.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
public class SecurityConfig {
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    @Value("${google.api.logout.url}")
    private String googleSignOutUrl;

    public SecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/auth/logout", "api/user", googleSignOutUrl).authenticated()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/api/v1/users").authenticated()
                        .anyRequest().authenticated()
                )
                 .logout(l -> l
                         .logoutSuccessUrl("/").permitAll())
                .oauth2Login()
                        .defaultSuccessUrl("/user").successHandler(authenticationSuccessHandler);
        return http.build();
    }
}
