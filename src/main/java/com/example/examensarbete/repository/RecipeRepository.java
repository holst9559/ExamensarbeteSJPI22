package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findByTitle(String title);
    List<Recipe> searchByRecipeIngredientsIn(Collection<Set<RecipeIngredient>> ingredients);
    List<Recipe> findByUserId(Long userId);
}
