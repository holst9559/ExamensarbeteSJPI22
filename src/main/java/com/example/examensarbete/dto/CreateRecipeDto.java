package com.example.examensarbete.dto;

import com.example.examensarbete.entities.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateRecipeDto(
        @NotEmpty
        String title,
        @NotEmpty
        Dish dish,
        @NotEmpty
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
        Set<Instruction> instructions,
        Set<RecipeIngredient> recipeIngredients,
        String imgUrl,
        Diet diet
) {
}
