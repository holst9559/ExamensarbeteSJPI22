package com.example.examensarbete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import com.example.examensarbete.repository.RecipeIngredientRepository;
import com.example.examensarbete.repository.RecipeRepository;
import com.example.examensarbete.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository repository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @InjectMocks
    private RecipeService service;

    @Test
    void getRecipesWithSingleIngredient() {
        // Setup
        Recipe recipe1 = createRecipe("flour", "egg");
        Recipe recipe2 = createRecipe("flour");

        RecipeIngredient ingredient1 = createRecipeIngredient("flour");
        List<RecipeIngredient> mockIngredients = Arrays.asList(ingredient1);
        when(recipeIngredientRepository.findAll()).thenReturn(mockIngredients);

        // Mocking Repository Behavior
        when(repository.searchByRecipeIngredientsIn(Collections.singleton(new HashSet<>(mockIngredients))))
                .thenReturn(List.of(recipe1, recipe2));

        // Method Invocation and Assertion
        assertThat(service.getRecipesWithIngredients(List.of("flour"))).containsExactlyInAnyOrder(recipe2);
    }

    @Test
    void getRecipesWithMultipleIngredients() {
        // Setup
        Recipe recipe1 = createRecipe("flour", "egg");
        Recipe recipe2 = createRecipe("flour");

        RecipeIngredient ingredient1 = createRecipeIngredient("flour");
        RecipeIngredient ingredient2 = createRecipeIngredient("egg");
        List<RecipeIngredient> mockIngredients = Arrays.asList(ingredient1, ingredient2);
        when(recipeIngredientRepository.findAll()).thenReturn(mockIngredients);

        // Mocking Repository Behavior
        when(repository.searchByRecipeIngredientsIn(Collections.singleton(new HashSet<>(mockIngredients))))
                .thenReturn(List.of(recipe1, recipe2));

        // Method Invocation and Assertion
        assertThat(service.getRecipesWithIngredients(List.of("flour", "egg"))).containsExactlyInAnyOrder(recipe2, recipe1);
    }

    @Test
    void getRecipesWithIngredients() {
        // Setup
        Recipe recipe1 = createRecipe("flour", "egg");
        Recipe recipe2 = createRecipe("flour");

        RecipeIngredient ingredient1 = createRecipeIngredient("flour");
        RecipeIngredient ingredient2 = createRecipeIngredient("egg");
        RecipeIngredient ingredient3 = createRecipeIngredient("cheese");
        List<RecipeIngredient> mockIngredients = Arrays.asList(ingredient1, ingredient2);
        when(recipeIngredientRepository.findAll()).thenReturn(mockIngredients);

        // Mocking Repository Behavior
        when(repository.searchByRecipeIngredientsIn(Collections.singleton(new HashSet<>(mockIngredients))))
                .thenReturn(List.of(recipe1, recipe2));

        // Method Invocation and Assertion
        assertThat(service.getRecipesWithIngredients(List.of("flour", "egg", "cheese"))).containsExactlyInAnyOrder(recipe2, recipe1);
    }

    // Helper methods
    private Recipe createRecipe(String... ingredients) {
        Set<RecipeIngredient> recipeIngredients = new HashSet<>();
        for (String ingredient : ingredients) {
            recipeIngredients.add(createRecipeIngredient(ingredient));
        }
        Recipe recipe = new Recipe();
        recipe.setRecipeIngredients(recipeIngredients);
        return recipe;
    }

    private RecipeIngredient createRecipeIngredient(String ingredientName) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientName);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        return recipeIngredient;
    }
}




