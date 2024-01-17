package com.example.examensarbete.controller;

import com.example.examensarbete.dto.CreateRecipeDto;
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
    public Recipe getRecipeById(@PathVariable Long id){
        return recipeService.getRecipeById(id);
    }

    @GetMapping("/{title}")
    public Recipe getRecipeByTitle(@PathVariable String title){
        return recipeService.getRecipeByTitle(title);
    }

    @GetMapping
    public List<Recipe> getRecipesWithIngredients(@RequestParam List<String> ingredients){
        return recipeService.getRecipesWithIngredients(ingredients);
    }

    @GetMapping
    public List<Recipe> getRecipesByUserId(@RequestParam Long userId){
        return recipeService.getRecipesByUserId(userId);
    }

    @PostMapping
    public Recipe addRecipe(@RequestBody @Validated CreateRecipeDto recipe){
        return recipeService.addRecipe(recipe);
    }
    /*

    @PatchMapping("/{id}")
    public Recipe editRecipe(@PathVariable int id,@RequestBody @Validated RecipeDto recipe){
        return recipeService.editRecipe(id, recipe);
    }

    @DeleteMapping("/{id}")
    public String deleteRecipe(@PathVariable int id){
        recipeService.deleteRecipe(id);
        return "Recipe with id: " + id + " was deleted.";
    }


     */
}
