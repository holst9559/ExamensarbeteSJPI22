package com.example.examensarbete.utils;

public record RegisterRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {
}

