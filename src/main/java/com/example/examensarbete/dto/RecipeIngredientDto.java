package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;

public record RecipeIngredientDto(
        String ingredientName,
        int amount,
        String unit
        ) {
}
