package com.example.examensarbete.repository;

import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.RecipeIngredient;
import com.example.examensarbete.entities.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    Optional<RecipeIngredient> findByIngredientAndUnitAndAmount(Ingredient ingredient, Unit unit, Double amount);
}
