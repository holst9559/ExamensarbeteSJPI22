package com.example.examensarbete.service;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.exception.RecipeAlreadyExistException;
import com.example.examensarbete.exception.RecipeNotFoundException;
import com.example.examensarbete.exception.UserNotFoundException;
import com.example.examensarbete.repository.*;
import com.example.examensarbete.utils.AuthenticationFacade;
import com.example.examensarbete.utils.RecipeCreator;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeCreator recipeCreator;
    private final AuthenticationFacade authenticationFacade;

    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository,
                         RecipeIngredientRepository recipeIngredientRepository,
                         RecipeCreator recipeCreator,
                         AuthenticationFacade authenticationFacade) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeCreator = recipeCreator;
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
            logger.info("Returned public and current user recipes");
            return Stream.concat(publicRecipes.stream(), userRecipes.stream()).distinct().toList();
        }
        logger.info("Returned public recipes");
        return recipeRepository.findByVisible(true);
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with id: '{}'",id);
                    return new RecipeNotFoundException(id);
                });
    }

    public Recipe getRecipeByTitle(String title) {
        return recipeRepository.findByTitle(title)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with title: '{}'",title);
                    return new RecipeNotFoundException(title);
                });
    }

    public List<Recipe> getRecipesWithIngredients(List<String> ingredients) {
        Set<String> userRoles = authenticationFacade.getRoles();

        if (userRoles.contains("ROLE_ADMIN")) {
            logger.info("Returned all recipes, Admin call");
            return recipeRepository.searchByRecipeIngredientsIn(Collections.singleton(getFilteredRecipeIngredients(ingredients)));
        }
        //Check for mail validation??
        String userEmail = authenticationFacade.getEmail();
        List<Recipe> userRecipes = recipeRepository.findByUserEmail(userEmail);
        List<Recipe> publicRecipes = recipeRepository.findByVisibleAndRecipeIngredientsIn(true, Collections.singleton(getFilteredRecipeIngredients(ingredients)));

        return Stream.concat(userRecipes.stream(), publicRecipes.stream()).distinct().toList();
    }

    private Set<RecipeIngredient> getFilteredRecipeIngredients(List<String> ingredients) {
        List<RecipeIngredient> filteredIngredients = recipeIngredientRepository.findAll();
        return filteredIngredients.stream()
                .filter(recipeIngredient -> ingredients.contains(recipeIngredient.getIngredientName()))
                .collect(Collectors.toSet());
    }

    public List<Recipe> getRecipesByUserId(Long userId) {
        Set<String> userRoles = authenticationFacade.getRoles();
        String userEmail = authenticationFacade.getEmail();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);

        if (userRoles.contains("ROLE_ADMIN") || userId.equals(user.getId())) {
            logger.info("Returned all recipes from userId: '{}'", userId);
            return recipeRepository.findByUserId(userId);
        } else {
            return recipeRepository.findByVisibleAndUserId(true, userId);
        }
    }

    @Transactional
    public Recipe addRecipe(@Validated CreateRecipeDto createRecipeDto) {
        String userEmail = authenticationFacade.getEmail();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        String title = createRecipeDto.title();
        recipeRepository.findByTitle(title)
                .ifPresent(existingRecipe -> {
                    logger.error("Recipe with title: '{}' already exist", title);
                    throw new RecipeAlreadyExistException(title);
                });

        logger.info("Recipe was created with title: '{}", title);
        Recipe recipe = recipeCreator.createRecipe(createRecipeDto, user);
        return recipeRepository.save(recipe);

    }

    @Transactional
    public Recipe editRecipe(Long id, @Validated RecipeDto recipeDto) {
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with id: '{}", id);
                    return new RecipeNotFoundException(id);
                });

        if (isUserAuthorized(recipe)) {
            Recipe updatedRecipe = updateRecipe(recipe, recipeDto);
            logger.info("Recipe updated with id: '{}'", id);
            return recipeRepository.save(updatedRecipe);
        } else {
            logger.error("User not authorized to edit recipe with id: '{}'", id);
            throw new RecipeNotFoundException(id);
        }
    }

    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with id: '{}", id);
                    return new RecipeNotFoundException(id);
                });

        if (isUserAuthorized(recipe)) {
            try {
                recipeRepository.deleteById(id);
                logger.info("Recipe deleted with id: '{}'", id);
            } catch (Exception e){
                logger.error("Failed to delete recipe with id: '{}'", id, e);
            }
        } else {
            logger.error("User not authorized to delete recipe with id: '{}'", id);
            throw new RecipeNotFoundException(id);
        }
    }

    private boolean isUserAuthorized(Recipe recipe) {
        String userEmail = authenticationFacade.getEmail();
        Set<String> userRoles = authenticationFacade.getRoles();
        System.out.println(userEmail);
        if(userEmail != null){
            return userRoles.contains("ROLE_ADMIN") || userEmail.equals(recipe.getUser().getEmail());
        }else{
            return false;
        }
    }

}
