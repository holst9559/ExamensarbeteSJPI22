package com.example.examensarbete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import com.example.examensarbete.repository.RecipeRepository;
import com.example.examensarbete.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository repository;

    @InjectMocks
    private RecipeService service;

    @Test
    void getRecipesWithSingleIngredient() {
        // Setup
        Recipe recipe1 = createRecipe("flour", "egg");
        Recipe recipe2 = createRecipe("flour");

        // Mocking Repository Behavior
        when(repository.searchByIngredients(List.of("flour"))).thenReturn(List.of(recipe1, recipe2));

        // Method Invocation and Assertion
        assertThat(service.getRecipesWithIngredients(List.of("flour"))).containsExactlyInAnyOrder(recipe2);
    }

    @Test
    void getRecipesWithMultipleIngredients() {
        // Setup
        Recipe recipe1 = createRecipe("flour", "egg");
        Recipe recipe2 = createRecipe("flour");

        // Mocking Repository Behavior
        when(repository.searchByIngredients(List.of("flour", "egg"))).thenReturn(List.of(recipe1, recipe2));

        // Method Invocation and Assertion
        assertThat(service.getRecipesWithIngredients(List.of("flour", "egg"))).containsExactlyInAnyOrder(recipe2, recipe1);
    }

    @Test
    void getRecipesWithAdditionalIngredient() {
        // Setup
        Recipe recipe1 = createRecipe("flour", "egg");
        Recipe recipe2 = createRecipe("flour");

        // Mocking Repository Behavior
        when(repository.searchByIngredients(List.of("flour", "egg", "cheese"))).thenReturn(List.of(recipe1, recipe2));

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


