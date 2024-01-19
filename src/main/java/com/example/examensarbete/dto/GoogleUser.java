package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;


public record GoogleUser(
        String givenName,
        String familyName,
        String fullName,
        @NotEmpty
        String email,
        String picture

) {
}
