package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;

public record RecipeIngredientDto(
        String ingredientName,
        Double amount,
        String unit
        ) {
}
