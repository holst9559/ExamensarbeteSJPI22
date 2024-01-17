package com.example.examensarbete.service;

import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository){
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id){
        return recipeRepository.findById(id).orElseThrow(RuntimeException::new);
    }




}
