package com.example.examensarbete.service;

import com.example.examensarbete.dto.GoogleUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Base64;
import java.util.Map;

@Service
public class AuthService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${google.api.logout.url}")
    private String googleSignOutUrl;

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final WebClient webClient;

    public AuthService(OAuth2AuthorizedClientService oAuth2AuthorizedClientService,
                       WebClient.Builder webClientBuilder){
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.webClient = webClientBuilder.baseUrl(googleSignOutUrl).build();
    }

    public GoogleUser getUserData(@AuthenticationPrincipal OAuth2User principal){
        Map<String, Object> attributes = principal.getAttributes();

        String givenName = getAttribute(attributes, "given_name", String.class);
        String familyName = getAttribute(attributes, "family_name", String.class);
        String fullName = getAttribute(attributes, "name", String.class);
        String id = getAttribute(attributes, "sub", String.class);
        String email = getAttribute(attributes, "email", String.class);
        String picture = getAttribute(attributes, "picture", String.class);

        return new GoogleUser(id, givenName, familyName, fullName, email, picture);
    }

    private <T> T getAttribute(Map<String, Object> attributeObj, String name, Class<T> type) {
        Object attribute = attributeObj.get(name);

        if (type.isInstance(attribute)) {
            return type.cast(attribute);
        } else {
            logger.warn("Invalid or missing attribute '{}': {}", name, attribute);
            return null;
        }
    }

    //FIX THIS
    public boolean logout(){
        String accessTokenValue = retrieveAccessToken();
        if(accessTokenValue != null){
            HttpHeaders headers = createAuthHeader();
            String jsonBody = "{\"access_token\":\"" + accessTokenValue + "\"}";

            try{
                webClient
                        .method(HttpMethod.DELETE)
                        .headers(httpHeaders -> httpHeaders.addAll(headers))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(jsonBody))
                        .retrieve()
                        .toBodilessEntity()
                        .block();
                clearAuthenticationCookies();
                return true;
            }catch (HttpClientErrorException.UnprocessableEntity e) {
                logger.error("Couldn't sign out from Google: , {}", e.getMessage());
            } catch (Exception e) {
                logger.error("Exception during logout: , {}", e.getMessage());
            }
        }else {
            logger.warn("Couldn't find access token");
        }
        return false;
    }
    //

    private HttpHeaders createAuthHeader() {
        String basicAuth = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basicAuth);
        return headers;
    }

    public String retrieveAccessToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof OAuth2AuthenticationToken token) {
            String clientRegistrationId = token.getAuthorizedClientRegistrationId();
            OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(
                    clientRegistrationId, token.getName());
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

            return accessToken.getTokenValue();
        }
        return null;
    }

    private void clearAuthenticationCookies() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest req = attributes.getRequest();
        HttpServletResponse res = attributes.getResponse();

        if (res != null) {
            Cookie[] cookies = req.getCookies();
            System.out.println(cookies);

            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("JSESSIONID")) {
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        res.addCookie(cookie);
                    }
                }
            }
        }
    }
}
