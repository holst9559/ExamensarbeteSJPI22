package com.example.examensarbete.dto;

import com.example.examensarbete.entities.Recipe;
import jakarta.validation.constraints.NotEmpty;

import javax.management.relation.Role;
import java.util.Set;

public record GoogleUser(
        Role role,
        String firstName,
        String lastName,
        String fullName,
        @NotEmpty
        String email,
        String pictureUrl,
        Set<Recipe> recipes

) {
}
