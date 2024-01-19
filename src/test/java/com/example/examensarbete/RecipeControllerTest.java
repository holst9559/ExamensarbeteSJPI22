package com.example.examensarbete;

import com.example.examensarbete.controller.RecipeController;
import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.InstructionDto;
import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.Instruction;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.AuthService;
import com.example.examensarbete.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashSet;
import java.util.Set;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RecipeController.class)
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testAddRecipe() throws Exception {
        // Arrange
        CreateRecipeDto recipeDto = new CreateRecipeDto(
                "Chicken Curry",
                "Main course",
                "indian",
                "Delicious chicken curry",
                20,
                30,
                4,
                true,
                Set.of(new InstructionDto("1", "prep food")
                        , new InstructionDto("2","Cook food")),
                Set.of(createRecipeIngredient("Chicken", 200, "gram"),
                        createRecipeIngredient("Spices", 5,"gram")),
                "https://example.com/chicken-curry.jpg",
                "Non vegetarian"
        );

        Recipe createdRecipe = createRecipe("Chicken", "Spices");
        when(recipeService.addRecipe(any(CreateRecipeDto.class))).thenReturn();

        // Act & Assert
        mockMvc.perform(post("/api/v1/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(recipeDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect((ResultMatcher) jsonPath("$.title", is("Chicken Curry")));
    }

    // Utility method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private RecipeIngredientDto createRecipeIngredient(String ingredientName, int amount, String unit ) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientName);
        return new RecipeIngredientDto(
                ingredientName,
                amount,
                unit);
    }
}

