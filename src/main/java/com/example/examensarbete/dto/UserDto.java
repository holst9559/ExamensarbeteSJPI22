package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;

import javax.management.relation.Role;
import java.util.List;

public record UserDto(
        Integer id,
        @NotEmpty
        String firstName,
        @NotEmpty
        String lastName,
        @NotEmpty
        String email,
        @NotEmpty
        List<String> roles,
        String username

) {
}
