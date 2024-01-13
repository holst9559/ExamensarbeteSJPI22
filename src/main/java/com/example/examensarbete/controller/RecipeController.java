package com.example.examensarbete.controller;

import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.service.RecipeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping
    public List<Recipe> getAllRecipes(){
        return recipeService.getAllRecipes();
    }

    @GetMapping("/{id}")
    public Recipe getRecipeById(@PathVariable int id){
        return recipeService.getRecipeById(id);
    }

    @GetMapping("/{name}")
    public Recipe getRecipeByName(@PathVariable String name){
        return recipeService.getRecipeByName(name);
    }

    @GetMapping
    public List<Recipe> getRecipesWithIngredients(@RequestParam String ingredients){
        return recipeService.getRecipesWithIngredients(ingredients);
    }

    @GetMapping
    public List<Recipe> getUserRecipes(@RequestParam int userId){
        return recipeService.getUserRecipes(userId);
    }

    @PostMapping
    public Recipe addRecipe(@RequestBody @Validated RecipeDto recipe){
        return recipeService.addRecipe(recipe);
    }

    @PatchMapping("/{id}")
    public Recipe editRecipe(@PathVariable int id,@RequestBody @Validated RecipeDto recipe){
        return recipeService.editRecipe(id, recipe);
    }

    @DeleteMapping("/{id}")
    public String deleteRecipe(@PathVariable int id){
        recipeService.deleteRecipe(id);
        return "Recipe with id: " + id + " was deleted.";
    }

}
