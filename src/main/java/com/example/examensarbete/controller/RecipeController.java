package com.example.examensarbete.controller;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
//@CrossOrigin("http://localhost:3000/")
@RequestMapping("api/v1/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public List<Recipe> getAllPublicRecipes(){
        return recipeService.getAllPublicRecipes();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<Recipe> getAllRecipes(){
        return recipeService.getAllRecipes();
    }

    @Transactional
    @GetMapping("/{id:\\d+}")
    public Recipe getRecipeById(@PathVariable Integer id){
        return recipeService.getRecipeById(id);
    }

    @GetMapping("/{title:.*\\D.*}")
    public Recipe getRecipeByTitle(@PathVariable String title){
        return recipeService.getRecipeByTitle(title);
    }

    @GetMapping("/search")
    public List<Recipe> getRecipesWithIngredients(@RequestParam(value = "ingredients") List<String> ingredients){
        return recipeService.getRecipesWithIngredients(ingredients);
    }

    @GetMapping("/user/{userId:\\d+}")
    public List<Recipe> getRecipesByUserId(@PathVariable Integer userId){
        return recipeService.getRecipesByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody @Validated CreateRecipeDto recipeDto, HttpServletRequest request){
        var created = recipeService.addRecipe(recipeDto, request);

        URI locationURI = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(locationURI).body(created);
    }

    @PatchMapping("/{id:\\d+}")
    public ResponseEntity<Recipe> editRecipe(@PathVariable Integer id,@RequestBody @Validated RecipeDto recipeDto){
        return ResponseEntity.ok().body(recipeService.editRecipe(id, recipeDto));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Integer id){
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

}
