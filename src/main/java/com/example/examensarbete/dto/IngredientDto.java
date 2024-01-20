package com.example.examensarbete.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record IngredientDto(
        @NotNull
        Long id,
        @NotEmpty
        String name
) implements Serializable {
}
