package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;

import javax.management.relation.Role;

public record UserDto(
        @NotEmpty
        Role role,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @NotEmpty
        String email

) {
}
