package com.example.examensarbete.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.Customizer.withDefaults;


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
        return http
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/auth/logout", "api/user", googleSignOutUrl).authenticated()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/v1/users").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(withDefaults())
                .formLogin(withDefaults())
                .build();
    }


/*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrf) -> csrf
                        .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/auth/logout", "/api/user", googleSignOutUrl).authenticated()
                        .requestMatchers(HttpMethod.GET, "/login", "/images/**").permitAll()
                        .requestMatchers("/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/logout").authenticated()
                        .anyRequest().authenticated()
                )
                .logout(l -> l
                        .logoutSuccessUrl("/").permitAll()
                )
                .oauth2Login(login -> login
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler)
                );

        return http.build();
    }

 */


}
