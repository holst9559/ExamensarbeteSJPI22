package com.example.examensarbete.dto;

import com.example.examensarbete.entities.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateRecipeDto(
        @NotEmpty
        String title,
        @NotNull
        Dish dish,
        @NotNull
        Category category,
        @NotEmpty
        String description,
        @NotNull
        Integer prepTime,
        @NotNull
        Integer cookTime,
        @NotNull
        Integer servings,
        @NotNull
        Boolean visible,
        @NotNull
        Set<InstructionDto> instructions,
        @NotNull
        Set<RecipeIngredientDto> recipeIngredients,
        String imgUrl,
        @NotNull
        Diet diet
) {
}
