package com.example.examensarbete.utils;

public record AuthenticationRequest(
        String email,
        String password
) {
}
