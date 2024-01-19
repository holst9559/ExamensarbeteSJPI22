package com.example.examensarbete;

import com.example.examensarbete.controller.RecipeController;
import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.InstructionDto;
import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@AutoConfigureMockMvc
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecipeService recipeService; // You may need to mock this if required

    @Test
    @WithMockUser(username = "testUser", roles = "USER")
    public void testAddRecipeEndpoint() throws Exception {
        // Create a sample CreateRecipeDto
        CreateRecipeDto createRecipeDto = new CreateRecipeDto(
                "Chicken Curry",
                new Dish("Main Course"),
                new Category("Indian"),
                "Delicious curry",
                20,30,
                4,
                true,
                Set.of(new InstructionDto("1","prep food"), new InstructionDto("2", "cook food")),
                Set.of(createRecipeIngredient("Chicken", 200, "gram"), createRecipeIngredient("Spices", 5, "gram")),
                "https://example.com/chicken-curry.jpg",
                new Diet("Non vegetarian")
        );
        // Set properties of createRecipeDto as needed

        // Mocking the behavior of recipeService.addRecipe method
        Recipe mockedRecipe = createRecipe("Chicken","Spices"); // Create a mocked recipe as needed
        // You can use a mocking framework like Mockito to mock behavior
        when(recipeService.addRecipe(createRecipeDto)).thenReturn(mockedRecipe);

        // Convert the createRecipeDto to JSON
        String createRecipeDtoJson = objectMapper.writeValueAsString(createRecipeDto);

        // Perform the POST request to the addRecipe endpoint
        ResultActions resultActions = mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRecipeDtoJson));

        // Verify the response
        resultActions
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(createRecipeDto.title()))
                .andExpect(jsonPath("$.user.username").value("testUser")); // Assuming there is a username field in the User class

        // Additional verification steps can be added based on your requirements
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
            recipeIngredients.add(new RecipeIngredient(new Ingredient(1L, ingredient),new Unit("gram"),200));
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

