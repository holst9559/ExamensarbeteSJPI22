package com.example.examensarbete.service;

import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository){
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }


}
