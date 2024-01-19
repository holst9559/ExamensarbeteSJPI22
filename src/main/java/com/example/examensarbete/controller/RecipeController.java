package com.example.examensarbete.controller;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.GoogleUser;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.AuthService;
import com.example.examensarbete.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public RecipeController(RecipeService recipeService,
                            AuthService authService,
                            UserRepository userRepository){
        this.recipeService = recipeService;
        this.userRepository = userRepository;
        this.authService = authService;
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

    @GetMapping("/search")
    public List<Recipe> getRecipesWithIngredients(@RequestParam(value = "ingredients") List<String> ingredients){
        return recipeService.getRecipesWithIngredients(ingredients);
    }

    @GetMapping("/{userId}")
    public List<Recipe> getRecipesByUserId(@PathVariable Long userId){
        return recipeService.getRecipesByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody @Validated CreateRecipeDto recipeDto){
        var created = recipeService.addRecipe(recipeDto);

        URI locationURI = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(locationURI).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Recipe> editRecipe(@PathVariable Long id,@RequestBody @Validated RecipeDto recipeDto){
        return ResponseEntity.ok().body(recipeService.editRecipe(id, recipeDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id){
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

}
