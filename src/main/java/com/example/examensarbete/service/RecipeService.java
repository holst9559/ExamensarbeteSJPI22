package com.example.examensarbete.service;

import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import com.example.examensarbete.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Recipe getRecipeByTitle(String title){
        return recipeRepository.findByTitle(title).orElseThrow(RuntimeException::new);
    }

    public List<Recipe> getRecipesWithIngredients(List<String> ingredients){
        List<Recipe> matchingRecipes = recipeRepository.searchByIngredients(ingredients);

        return matchingRecipes.stream()
                .filter(recipe -> recipe.getRecipeIngredients().stream()
                        .map(RecipeIngredient::getIngredient)
                        .map(Ingredient::getName)
                        .allMatch(ingredients::contains))
                .toList();
    }

    public List<Recipe> getRecipesByUserId(Long userId){
        return recipeRepository.findByUserId(userId);
    }







}
