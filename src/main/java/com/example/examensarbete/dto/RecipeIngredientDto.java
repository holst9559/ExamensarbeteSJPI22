package com.example.examensarbete.dto;

public record RecipeIngredientDto(
        IngredientDto ingredient,
        Double amount,
        String unit
) {
}
