package com.example.examensarbete.repository;

import com.example.examensarbete.entities.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    Ingredient findById(Long id);
    Ingredient findByName(String name);
}
