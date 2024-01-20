package com.example.examensarbete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.InstructionDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.repository.RecipeIngredientRepository;
import com.example.examensarbete.repository.RecipeRepository;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.service.RecipeService;
import com.example.examensarbete.utils.RecipeCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;
    @Mock
    private RecipeCreator recipeCreator;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void getRecipesWithMultipleIngredients() {
        // Setup
        Recipe recipe1 = mockRecipe(); //Pancakes
        Recipe recipe3 = mockRecipe3(); //Fried Chicken

        RecipeIngredient ingredient1 = mockRecipeIngredients().get(0);
        RecipeIngredient ingredient2 = mockRecipeIngredients().get(3);
        RecipeIngredient ingredient3 = mockRecipeIngredients().get(5);
        RecipeIngredient ingredient4 = mockRecipeIngredients().get(6);
        List<RecipeIngredient> mockIngredients = Arrays.asList(ingredient1, ingredient2, ingredient3, ingredient4);
        when(recipeIngredientRepository.findAll()).thenReturn(mockIngredients);

        // Mocking Repository Behavior
        when(recipeRepository.searchByRecipeIngredientsIn(Collections.singleton(new HashSet<>(mockIngredients))))
                .thenReturn(List.of(recipe1, recipe3));

        // Method Invocation and Assertion
        assertThat(recipeService.getRecipesWithIngredients(List.of("flour", "eggs", "panko", "chicken", "beef"))).containsExactlyInAnyOrder(recipe1,recipe3);
    }

    @Test
    void getRecipesWithIngredients() {
        // Setup
        Recipe recipe1 = mockRecipe(); //Pancakes
        Recipe recipe3 = mockRecipe3(); //Fried Chicken

        RecipeIngredient ingredient1 = mockRecipeIngredients().get(2);
        RecipeIngredient ingredient2 = mockRecipeIngredients().get(4);
        List<RecipeIngredient> mockIngredients = Arrays.asList(ingredient1, ingredient2);
        when(recipeIngredientRepository.findAll()).thenReturn(mockIngredients);

        // Mocking Repository Behavior
        when(recipeRepository.searchByRecipeIngredientsIn(Collections.singleton(new HashSet<>(mockIngredients))))
                .thenReturn(List.of(recipe1, recipe3));

        // Method Invocation and Assertion
        assertThat(recipeService.getRecipesWithIngredients(List.of("flour", "eggs"))).containsExactlyInAnyOrder(recipe1);
    }

    @Test
    void getAllRecipes() {
        // Mocking Repository Behavior
        Recipe recipe1 = mockRecipe(); //Pancakes
        Recipe recipe2 = mockRecipe2(); //Tofu Curry
        Recipe recipe3 = mockRecipe3();

        when(recipeRepository.findAll()).thenReturn(Arrays.asList(
                recipe1, recipe2, recipe3
        ));

        // Method Invocation and Assertion
        List<Recipe> recipes = recipeService.getAllRecipes();
        assertThat(recipes).hasSize(3);
    }

    @Test
    void getRecipeById() {
        // Mocking Repository Behavior
        Long recipeId = 1L;
        Recipe recipe1 = mockRecipe(); //Pancakes
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe1));

        // Method Invocation and Assertion
        Recipe recipe = recipeService.getRecipeById(recipeId);
        assertThat(recipe).isEqualTo(recipe1);
    }

    @Test
    void getRecipeByTitle() {
        // Mocking Repository Behavior
        String recipeTitle = "Pancakes";
        Recipe recipe1 = mockRecipe(); //Pancakes
        when(recipeRepository.findByTitle(recipeTitle)).thenReturn(Optional.of(recipe1));

        // Method Invocation and Assertion
        Recipe recipe = recipeService.getRecipeByTitle(recipeTitle);
        assertThat(recipe).isEqualTo(recipe1);
    }

    @Test
    void getRecipesByUserId_success() {
        // Mocking Repository Behavior
        Long userId = 1L;
        when(recipeRepository.findByUserId(userId)).thenReturn(List.of(mockRecipe(),mockRecipe2()
        ));

        // Method Invocation and Assertion
        List<Recipe> recipes = recipeService.getRecipesByUserId(userId);
        assertThat(recipes).hasSize(2);
    }

    @Test
    void getRecipesByUserId_userNotFound() {
        // Mocking Repository Behavior
        Long userId = 2L;
        when(recipeRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Method Invocation and Assertion
        List<Recipe> recipes = recipeService.getRecipesByUserId(userId);
        assertThat(recipes).isEmpty();
    }

    @Test
    void addRecipe_Successful() {
        // Mocking Repository Behavior
        CreateRecipeDto recipeDto = createRecipeDto();
        Recipe recipe1 = mockRecipe(); //Pancakes
        User user = newUser(recipe1);

        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(auth.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(recipeRepository.findByTitle(recipeDto.title())).thenReturn(Optional.empty());

        Recipe createdRecipe = mockRecipe();
        when(recipeCreator.createRecipe(recipeDto, user)).thenReturn(createdRecipe);
        when(recipeRepository.save(createdRecipe)).thenReturn(createdRecipe);

        // Method Invocation and Assertion
        Recipe addedRecipe = recipeService.addRecipe(recipeDto);
        assertEquals(createdRecipe, addedRecipe);
    }

    @Test
    void addRecipe_RecipeAlreadyExists() {
        // Mocking Repository Behavior
        Recipe recipe1 = mockRecipe();
        CreateRecipeDto recipeDto = createRecipeDto();
        User user = newUser(recipe1);

        Authentication auth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);
        when(auth.getName()).thenReturn(user.getEmail());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(recipeRepository.findByTitle(recipeDto.title())).thenReturn(Optional.of(mockRecipe()));

        // Method Invocation and Assertion
        assertThrows(IllegalArgumentException.class, () -> recipeService.addRecipe(recipeDto));
    }

    @Test
    void editRecipe_Successful() {
        // Mocking Repository Behavior
        Long recipeId = 1L;
        RecipeDto recipeDto = RecipeDto();
        Recipe existingRecipe = mockRecipe();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Method Invocation and Assertion
        Recipe editedRecipe = recipeService.editRecipe(recipeId, recipeDto);
        assertNotNull(editedRecipe, "Edited recipe should not be null");
        assertEquals(recipeDto.title(), editedRecipe.getTitle(), "Title should be updated");
        assertEquals(10,editedRecipe.getServings(), "Servings should be updated");
    }

    @Test
    void editRecipe_RecipeNotFound() {
        // Mocking Repository Behavior
        Long recipeId = 1L;
        RecipeDto recipeDto = RecipeDto();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> recipeService.editRecipe(recipeId, recipeDto));
    }

    @Test
    void deleteRecipe_Successful() {
        // Mocking Repository Behavior
        Long recipeId = 1L;
        Recipe existingRecipe = mockRecipe();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));


        // Method Invocation
        recipeService.deleteRecipe(recipeId);

        // Verification
        verify(recipeRepository, times(1)).deleteById(recipeId);
    }

    @Test
    void deleteRecipe_RecipeNotFound() {
        // Mocking Repository Behavior
        Long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        // Method Invocation and Assertion
        assertThrows(RuntimeException.class, () -> recipeService.deleteRecipe(recipeId));
    }


    // Helper methods
    private CreateRecipeDto createRecipeDto(){
        Set<InstructionDto> instructions = new HashSet<>();
        instructions.add(createNewInstructionDto("1", "Prep"));
        instructions.add(createNewInstructionDto("2", "Cook"));

        Set<RecipeIngredientDto> recipeIngredients = new HashSet<>();
        recipeIngredients.add(createRecipeIngredientDto("flour", 200, "gram"));
        recipeIngredients.add(createRecipeIngredientDto("eggs", 2, "pcs"));

        return new CreateRecipeDto("Pancakes",
                new Dish("Other"),
                new Category("Breakfast"),
                "Tasty pancakes",
                10,
                20,
                4,
                true,
                instructions,
                recipeIngredients,
                "https://plus.unsplash.com/premium_photo-1672846027109-e2c91500afef?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                new Diet("VEGETARIAN")
                );
    }

    private RecipeDto RecipeDto(){
        Set<Instruction> instructions = new HashSet<>();
        instructions.add(createNewInstruction("1", "Prep"));
        instructions.add(createNewInstruction("2", "Cook"));

        Set<RecipeIngredient> recipeIngredients = new HashSet<>();
        recipeIngredients.add(createRecipeIngredient(1L,"flour", 200, "gram"));
        recipeIngredients.add(createRecipeIngredient(4L,"eggs", 2, "pcs"));

        return new RecipeDto(1L,
                "Pancakes",
                newUser(null),
                new Dish("Other"),
                new Category("Breakfast"),
                "Tasty pancakes",
                10,
                20,
                10,
                true,
                instructions,
                recipeIngredients,
                "https://plus.unsplash.com/premium_photo-1672846027109-e2c91500afef?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                new Diet("VEGETARIAN")

        );
    }


    private RecipeIngredient createRecipeIngredient(Long id, String ingredientName, int amount, String unit) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientName);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setAmount(amount);
        recipeIngredient.setUnit(new Unit(unit));
        recipeIngredient.setId(id);
        return recipeIngredient;
    }

    private Instruction createNewInstruction(String step, String name){
        Instruction instruction = new Instruction();
        instruction.setStep(step);
        instruction.setDescription(name);
        return instruction;
    }


    private InstructionDto createNewInstructionDto(String step, String name){
        return new InstructionDto(
                step,
                name
        );
    }

    private RecipeIngredientDto createRecipeIngredientDto(String ingredientName, int amount, String unit) {
        return new RecipeIngredientDto(
                ingredientName,
                amount,
                unit
        );
    }

    private Recipe mockRecipe() {
        Recipe recipe = new Recipe();

        recipe.setTitle("Pancakes");
        recipe.setServings(4);

        Set<Instruction> instructionsSet = new HashSet<>();
        instructionsSet.add(createNewInstruction("1", "Prep"));
        instructionsSet.add(createNewInstruction("2", "Cook"));
        recipe.setInstructions(instructionsSet);

        recipe.setCategory(new Category("Other"));
        recipe.setDiet(new Diet("VEGETARIAN"));
        recipe.setImgUrl("https://plus.unsplash.com/premium_photo-1672846027109-e2c91500afef?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        recipe.setDish(new Dish("Breakfast"));
        recipe.setVisible(true);
        recipe.setCookTime(10);
        recipe.setPrepTime(20);
        recipe.setId(1L);
        recipe.setDescription("Easy pancakes");
        Set<RecipeIngredient> recipeIngredients = new HashSet<>();

        RecipeIngredient ingredient1 = mockRecipeIngredients().get(0);
        RecipeIngredient ingredient2 = mockRecipeIngredients().get(3);
        recipeIngredients.add(ingredient1);
        recipeIngredients.add(ingredient2);
        recipe.setRecipeIngredients(recipeIngredients);

        recipe.setUser(newUser(recipe));

        return recipe;
    }

    private Recipe mockRecipe2() {
        Recipe recipe = new Recipe();

        recipe.setTitle("Tofu Curry");
        recipe.setServings(4);

        Set<Instruction> instructionsSet = new HashSet<>();
        instructionsSet.add(createNewInstruction("1", "Prep"));
        instructionsSet.add(createNewInstruction("2", "Cook"));
        recipe.setInstructions(instructionsSet);

        recipe.setCategory(new Category("Stew"));
        recipe.setDiet(new Diet("VEGETARIAN"));
        recipe.setImgUrl("https://plus.unsplash.com/premium_photo-1672846027109-e2c91500afef?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        recipe.setDish(new Dish("Main Course"));
        recipe.setVisible(true);
        recipe.setCookTime(10);
        recipe.setPrepTime(20);
        recipe.setId(1L);
        recipe.setDescription("Tasty tofu curry");
        Set<RecipeIngredient> recipeIngredients = new HashSet<>();

        RecipeIngredient ingredient1 = mockRecipeIngredients().get(2);
        RecipeIngredient ingredient2 = mockRecipeIngredients().get(8);
        RecipeIngredient ingredient3 = mockRecipeIngredients().get(9);

        recipeIngredients.add(ingredient1);
        recipeIngredients.add(ingredient2);
        recipeIngredients.add(ingredient3);

        recipe.setRecipeIngredients(recipeIngredients);

        recipe.setUser(newUser(recipe));

        return recipe;
    }

    private Recipe mockRecipe3() {
        Recipe recipe = new Recipe();

        recipe.setTitle("Fried Chicken");
        recipe.setServings(4);

        Set<Instruction> instructionsSet = new HashSet<>();
        instructionsSet.add(createNewInstruction("1", "Prep"));
        instructionsSet.add(createNewInstruction("2", "Cook"));
        recipe.setInstructions(instructionsSet);

        recipe.setCategory(new Category("Other"));
        recipe.setDiet(new Diet("OMNIVORE"));
        recipe.setImgUrl("https://plus.unsplash.com/premium_photo-1672846027109-e2c91500afef?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
        recipe.setDish(new Dish("Main Course"));
        recipe.setVisible(true);
        recipe.setCookTime(10);
        recipe.setPrepTime(20);
        recipe.setId(1L);
        recipe.setDescription("Crispy chicken");
        Set<RecipeIngredient> recipeIngredients = new HashSet<>();

        RecipeIngredient ingredient1 = mockRecipeIngredients().get(1);
        RecipeIngredient ingredient2 = mockRecipeIngredients().get(4);
        RecipeIngredient ingredient3 = mockRecipeIngredients().get(5);
        RecipeIngredient ingredient4 = mockRecipeIngredients().get(6);

        recipeIngredients.add(ingredient1);
        recipeIngredients.add(ingredient2);
        recipeIngredients.add(ingredient3);
        recipeIngredients.add(ingredient4);
        recipe.setRecipeIngredients(recipeIngredients);

        recipe.setUser(newUser(recipe));

        return recipe;
    }


    private User newUser(Recipe recipes) {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@google.com");
        user.setFirstName("Anton");
        user.setLastName("Holst");
        user.setFullName("Anton Holst");
        Set<Recipe> recipeSet = new HashSet<>();
        recipeSet.add(recipes);
        user.setRecipes(recipeSet);

        return user;
    }

    private List<RecipeIngredient> mockRecipeIngredients(){
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        RecipeIngredient ingredient1 = createRecipeIngredient(1L,"flour", 200, "gram");
        RecipeIngredient ingredient2 = createRecipeIngredient(2L,"flour", 50, "gram");
        RecipeIngredient ingredient3 = createRecipeIngredient(3L,"flour", 15, "gram");
        RecipeIngredient ingredient4 = createRecipeIngredient(4L,"eggs", 2, "pcs");
        RecipeIngredient ingredient5 = createRecipeIngredient(5L,"eggs", 3, "pcs");
        RecipeIngredient ingredient6 = createRecipeIngredient(6L,"panko", 80, "gram");
        RecipeIngredient ingredient7 = createRecipeIngredient(7L,"chicken", 400, "gram");
        RecipeIngredient ingredient8 = createRecipeIngredient(8L,"beef", 200, "gram");
        RecipeIngredient ingredient9 = createRecipeIngredient(9L,"curry", 20, "gram");
        RecipeIngredient ingredient10 = createRecipeIngredient(10L,"tofu", 200, "gram");

        recipeIngredients.add(ingredient1);
        recipeIngredients.add(ingredient2);
        recipeIngredients.add(ingredient3);
        recipeIngredients.add(ingredient4);
        recipeIngredients.add(ingredient5);
        recipeIngredients.add(ingredient6);
        recipeIngredients.add(ingredient7);
        recipeIngredients.add(ingredient8);
        recipeIngredients.add(ingredient9);
        recipeIngredients.add(ingredient10);

        return recipeIngredients;
    }
}




