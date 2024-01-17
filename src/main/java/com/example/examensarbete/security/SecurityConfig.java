package com.example.examensarbete.security;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(Customizer.withDefaults())
                    .authorizeHttpRequests(requests -> requests
                            .requestMatchers(HttpMethod.GET, "/api/v1/ingredients").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/ingredients/*").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/recipes").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/recipes/*").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/users").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/v1/users/*").permitAll()
                        .anyRequest().denyAll())
                .build();
    }


    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

}
