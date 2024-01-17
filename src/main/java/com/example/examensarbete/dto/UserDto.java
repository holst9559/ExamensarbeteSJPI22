package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import javax.management.relation.Role;

public record UserDto(
        @NotEmpty
        Role role,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @NotNull
        String password,
        @NotEmpty
        String email

) {
}
