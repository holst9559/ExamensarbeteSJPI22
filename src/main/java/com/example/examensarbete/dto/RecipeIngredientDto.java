package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;

public record RecipeIngredientDto(
        IngredientDto ingredient,
        Double amount,
        String unit
        ) {
}
