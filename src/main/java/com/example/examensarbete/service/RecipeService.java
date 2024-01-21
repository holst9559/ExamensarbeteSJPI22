package com.example.examensarbete.service;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.repository.*;
import com.example.examensarbete.utils.AuthenticationFacade;
import com.example.examensarbete.utils.RecipeCreator;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeCreator recipeCreator;
    private final IngredientRepository ingredientRepository;
    private final AuthService authService;
    private final AuthenticationFacade authenticationFacade;

    public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository, RecipeIngredientRepository recipeIngredientRepository, RecipeCreator recipeCreator, IngredientRepository ingredientRepository, AuthService authService, AuthenticationFacade authenticationFacade) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeCreator = recipeCreator;
        this.ingredientRepository = ingredientRepository;
        this.authService = authService;
        this.authenticationFacade = authenticationFacade;
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

    public List<Recipe> getAllPublicRecipes() {
        String email = authenticationFacade.getEmail();

        if (email != null) {
            List<Recipe> publicRecipes = recipeRepository.findByVisible(true);
            List<Recipe> userRecipes = recipeRepository.findByUserEmail(email);
            return Stream.concat(publicRecipes.stream(), userRecipes.stream()).distinct().toList();
        }
        return recipeRepository.findByVisible(true);
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public Recipe getRecipeByTitle(String title) {
        return recipeRepository.findByTitle(title).orElseThrow(RuntimeException::new);
    }

    public List<Recipe> getRecipesWithIngredients(List<String> ingredients) {
        Set<String> userRoles = authenticationFacade.getRoles();

        if (userRoles.contains("OIDC_ADMIN")) {
            return recipeRepository.searchByRecipeIngredientsIn(Collections.singleton(getFilteredRecipeIngredients(ingredients)));
        }
        String userEmail = authenticationFacade.getEmail();
        List<Recipe> userRecipes = recipeRepository.findByUserEmail(userEmail);
        List<Recipe> publicRecipes = recipeRepository.findByVisibleAndRecipeIngredientsIn(true, Collections.singleton(getFilteredRecipeIngredients(ingredients)));

        return Stream.concat(userRecipes.stream(), publicRecipes.stream()).distinct().toList();

    }

    private Set<RecipeIngredient> getFilteredRecipeIngredients(List<String> ingredients) {
        List<RecipeIngredient> filteredIngredients = recipeIngredientRepository.findAll();
        return filteredIngredients.stream().filter(recipeIngredient -> {
            String ingredientName = recipeIngredient.getIngredientName();
            return ingredientName != null && ingredients.contains(ingredientName);
        }).collect(Collectors.toSet());
    }

    public List<Recipe> getRecipesByUserId(Long userId) {
        Set<String> userRoles = authenticationFacade.getRoles();
        String userEmail = authenticationFacade.getEmail();
        var userCheck = userRepository.findByEmail(userEmail);

        if(userCheck.isPresent()){
            if (userRoles.contains("OIDC_ADMIN") || userId.equals(userCheck.get().getId())) {
                return recipeRepository.findByUserId(userId);
            }else {
                return recipeRepository.findByVisibleAndUserId(true, userId);
            }

        }else {
            throw new RuntimeException("User not found");
        }


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
        String userEmail = authenticationFacade.getEmail();
        Set<String> userRoles = authenticationFacade.getRoles();

        if (recipeCheck.isPresent() && (userRoles.contains("OIDC_ADMIN") || userEmail.equals(recipeCheck.get().getUser().getEmail()))) {
            Recipe recipeToUpdate = updateRecipe(recipeCheck.get(), recipeDto);

            return recipeRepository.save(recipeToUpdate);
        } else {
            throw new RuntimeException("Recipe with the id: " + id + " was not found");
        }
    }

    @Transactional
    public void deleteRecipe(Long id) {
        var recipeCheck = recipeRepository.findById(id);
        String userEmail = authenticationFacade.getEmail();
        Set<String> userRoles = authenticationFacade.getRoles();

        if (recipeCheck.isPresent() && (userRoles.contains("OIDC_ADMIN") || userEmail.equals(recipeCheck.get().getUser().getEmail()))) {
            recipeRepository.deleteById(id);
        } else {
            throw new RuntimeException("Recipe with the id: " + id + " was not found");
        }
    }

}
