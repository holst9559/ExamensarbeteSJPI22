package com.example.examensarbete.service;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.entities.Recipe;
import com.example.examensarbete.entities.RecipeIngredient;
import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.RecipeRepository;
import com.example.examensarbete.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository){
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id){
        return recipeRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public Recipe getRecipeByTitle(String title){
        return recipeRepository.findByTitle(title).orElseThrow(RuntimeException::new);
    }

    public List<Recipe> getRecipesWithIngredients(List<String> ingredients){
        List<Recipe> matchingRecipes = recipeRepository.searchByIngredients(ingredients);

        return matchingRecipes.stream()
                .filter(recipe -> recipe.getRecipeIngredients().stream()
                        .map(RecipeIngredient::getIngredient)
                        .map(Ingredient::getName)
                        .allMatch(ingredients::contains))
                .toList();
    }

    public List<Recipe> getRecipesByUserId(Long userId){
        return recipeRepository.findByUserId(userId);
    }

    @Transactional
    public Recipe addRecipe(@Validated CreateRecipeDto createRecipeDto){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication(); //Fix this once OAuth2 is implemented
        String email = auth.getName();

        User user = userRepository.findByEmail(email).orElseThrow(RuntimeException::new);
        var recipeCheck = recipeRepository.findByTitle(createRecipeDto.title());
        if(recipeCheck.isEmpty()){
            Recipe recipe = createRecipe(createRecipeDto, user);
            return recipeRepository.save(recipe);
        }
        throw new IllegalArgumentException("Recipe with the title: " + createRecipeDto.title() + " already exist.");

    }

    @Transactional
    public Recipe editRecipe(Long id, @Validated RecipeDto recipeDto){
        var recipeCheck = recipeRepository.findById(id);

        if(recipeCheck.isPresent()){
            Recipe recipeToUpdate = updateRecipe(recipeCheck.get(), recipeDto);

            return recipeRepository.save(recipeToUpdate);
        }else {
            throw new RuntimeException("Ingredient with the id: " + id + " was not found");
        }

    }


    private static Recipe createRecipe(CreateRecipeDto createRecipeDto, User user) {
        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.setTitle(createRecipeDto.title());
        recipe.setDish(createRecipeDto.dish());
        recipe.setCategory(createRecipeDto.category());
        recipe.setDescription(createRecipeDto.description());
        recipe.setPrepTime(createRecipeDto.prepTime());
        recipe.setCookTime(createRecipeDto.cookTime());
        recipe.setServings(createRecipeDto.servings());
        recipe.setVisible(createRecipeDto.visible());
        recipe.setInstructions(createRecipeDto.instructions());
        recipe.setRecipeIngredients(createRecipeDto.recipeIngredients());
        recipe.setImgUrl(createRecipeDto.imgUrl());
        recipe.setDiet(createRecipeDto.diet());
        return recipe;
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


}
