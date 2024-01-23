package com.example.examensarbete.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    public SecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/*").authenticated()
                        .anyRequest().authenticated()
                )
                .logout(l -> l
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/login").permitAll())
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(userInfo -> userInfo
                                .userAuthoritiesMapper(userAuthoritiesMapper()))
                        .successHandler(authenticationSuccessHandler));
        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                GrantedAuthority mappedAuthority;
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    mappedAuthority = new OidcUserAuthority(
                            "OIDC_USER", ((OidcUserAuthority) authority).getIdToken(), ((OidcUserAuthority) authority).getUserInfo()
                    );
                    Map<String, Object> attributes = oidcUserAuthority.getAttributes();
                    String email = (String) attributes.get("email");

                    if (email.equals(adminEmail)) {
                        mappedAuthorities.add(new SimpleGrantedAuthority("OIDC_ADMIN"));
                    } else {
                        mappedAuthorities.add(mappedAuthority);
                    }
                }
            });
            return mappedAuthorities;
        };
    }
}
