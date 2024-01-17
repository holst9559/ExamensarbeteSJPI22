package com.example.examensarbete.repository;

import com.example.examensarbete.entities.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
}
