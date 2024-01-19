package com.example.examensarbete.repository;

import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;


public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    Optional<RecipeIngredient> findByNameUnitAmount(Set<RecipeIngredientDto> ingredientDtos);
}
