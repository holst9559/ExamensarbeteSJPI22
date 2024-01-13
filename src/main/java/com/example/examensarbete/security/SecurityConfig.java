package com.example.examensarbete.security;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                    .authorizeHttpRequests(requests -> requests
                            .requestMatchers(HttpMethod.GET, "/api/v1/ingredients").permitAll()
                            .requestMatchers(HttpMethod.GET,  "api/v1/ingredients/*").permitAll()
                        .anyRequest().permitAll())
                .build();
    }
}
