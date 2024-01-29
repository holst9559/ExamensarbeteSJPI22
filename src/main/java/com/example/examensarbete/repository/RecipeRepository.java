package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    Optional<Recipe> findByTitle(String title);
    List<Recipe> searchByRecipeIngredientsIn(Collection<Set<RecipeIngredient>> recipeIngredients);
    List<Recipe> findByVisibleAndRecipeIngredientsIn(Boolean visible, Collection<Set<RecipeIngredient>> recipeIngredients);
    List<Recipe> findByUserId(Integer userId);
    List<Recipe> findByVisibleAndUserId(Boolean visible, Integer userId);
    List<Recipe> findByVisible(Boolean visible);
    List<Recipe> findByUserEmail(String email);
}
