package com.example.examensarbete.service;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.repository.*;
import com.example.examensarbete.utils.RecipeCreator;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeCreator recipeCreator;

    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository,
                         RecipeIngredientRepository recipeIngredientRepository,
                         RecipeCreator recipeCreator) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeCreator = recipeCreator;
    }

    private static Recipe updateRecipe(Recipe recipe, RecipeDto recipeDto) {
        recipe.setTitle(recipeDto.title());
        recipe.setDish(recipeDto.dish());
        recipe.setCategory(recipeDto.category());
        recipe.setDescription(recipeDto.description());
        recipe.setPrepTime(recipeDto.prepTime());
        recipe.setCookTime(recipeDto.cookTime());
        recipe.setServings(recipeDto.servings());
        recipe.setVisible(recipeDto.visible());
        recipe.setInstructions(recipeDto.instructions());
        recipe.setRecipeIngredients(recipeDto.recipeIngredients());
        recipe.setImgUrl(recipeDto.imgUrl());
        recipe.setDiet(recipeDto.diet());
        return recipe;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public Recipe getRecipeByTitle(String title) {
        return recipeRepository.findByTitle(title).orElseThrow(RuntimeException::new);
    }

    public List<Recipe> getRecipesWithIngredients(List<String> ingredients) {
        List<RecipeIngredient> filteredIngredients = recipeIngredientRepository.findAll();
        Set<RecipeIngredient> filteredSet = filteredIngredients.stream()
                .filter(recipeIngredient -> {
                    String ingredientName = recipeIngredient.getIngredientName();
                    return ingredientName != null && ingredients.contains(ingredientName);
                })
                .collect(Collectors.toSet());

        List<Recipe> matchingRecipes = recipeRepository.searchByRecipeIngredientsIn(Collections.singleton(filteredSet));

        return matchingRecipes.stream()
                .filter(recipe -> recipe.getRecipeIngredients().stream()
                        .map(RecipeIngredient::getIngredient)
                        .map(Ingredient::getName)
                        .allMatch(ingredients::contains))
                .toList();
    }

    public List<Recipe> getRecipesByUserId(Long userId) {
        return recipeRepository.findByUserId(userId);
    }

    @Transactional
    public Recipe addRecipe(@Validated CreateRecipeDto createRecipeDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //Fix this once OAuth2 is implemented
        String email = auth.getName();

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        var recipeCheck = recipeRepository.findByTitle(createRecipeDto.title());
        if (recipeCheck.isEmpty()) {
            Recipe recipe = recipeCreator.createRecipe(createRecipeDto, user);
            return recipeRepository.save(recipe);
        }
        throw new IllegalArgumentException("Recipe with the title: " + createRecipeDto.title() + " already exist.");

    }

    @Transactional
    public Recipe editRecipe(Long id, @Validated RecipeDto recipeDto) {
        var recipeCheck = recipeRepository.findById(id);

        if (recipeCheck.isPresent()) {
            Recipe recipeToUpdate = updateRecipe(recipeCheck.get(), recipeDto);

            return recipeRepository.save(recipeToUpdate);
        } else {
            throw new RuntimeException("Recipe with the id: " + id + " was not found");
        }
    }

    @Transactional
    public void deleteRecipe(Long id) {
        var recipeCheck = recipeRepository.findById(id);

        if (recipeCheck.isEmpty()) {
            throw new RuntimeException("Recipe with the id: " + id + " was not found");
        }
        recipeRepository.deleteById(id);
    }


}
