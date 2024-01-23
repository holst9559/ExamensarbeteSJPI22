package com.example.examensarbete.service;

import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.exception.MissingUserAttributeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public GoogleUser getUserData(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();

        String givenName = getAttribute(attributes, "given_name", String.class);
        String familyName = getAttribute(attributes, "family_name", String.class);
        String fullName = getAttribute(attributes, "name", String.class);
        String email = getAttribute(attributes, "email", String.class);
        String picture = getAttribute(attributes, "picture", String.class);

        logger.info("Returned user data: 'givenName={}, familyName={}, fullName={}, email={}, picture={}'",
                givenName, familyName, fullName, email, picture);
        return new GoogleUser(givenName, familyName, fullName, email, picture);
    }

    private <T> T getAttribute(Map<String, Object> attributeObj, String name, Class<T> type) {
        Object attribute = attributeObj.get(name);

        if (type.isInstance(attribute)) {
            return type.cast(attribute);
        } else {
            logger.warn("Invalid or missing attribute '{}': {}", name, attribute);
            throw new MissingUserAttributeException(name);
        }
    }
}
