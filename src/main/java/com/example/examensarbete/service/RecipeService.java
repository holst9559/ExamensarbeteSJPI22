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
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.Key;
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
    @Value("${JWT_SECRET}")
    private String SECRET_KEY;

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

    @Transactional
    public Recipe getRecipeById(Integer id) {
         Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with id: '{}'",id);
                    return new RecipeNotFoundException(id);
                });
        Hibernate.initialize(recipe.getInstructions());
        Hibernate.initialize(recipe.getRecipeIngredients());

        return recipe;
    }

    @Transactional
    public Recipe getRecipeByTitle(String title) {
        return recipeRepository.findByTitle(title)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with title: '{}'",title);
                    return new RecipeNotFoundException(title);
                });
    }

    public List<Recipe> getRecipesWithIngredients(List<String> ingredients) {
        Set<String> userRoles = authenticationFacade.getRoles();

        List<RecipeIngredient> ingredientList = getFilteredRecipeIngredients(ingredients);

        if (userRoles.contains("ROLE_ADMIN")) {
            System.out.println("ADMIN USER");
            logger.info("Returned all recipes, Admin call");
            return recipeRepository.searchByRecipeIngredientsIn(Collections.singleton(new HashSet<>(ingredientList)));
        }

        //Check for mail validation??
        String userEmail = authenticationFacade.getEmail();
        System.out.println("BEFORE USER");
        List<Recipe> userRecipes = recipeRepository.findByUserEmail(userEmail);
        System.out.println("BEFORE PUBLIC");
        List<Recipe> publicRecipes = recipeRepository.findByVisibleAndRecipeIngredientsIn(true, Collections.singleton(new HashSet<>(ingredientList)));

        System.out.println("BEFORE CONCAT");
        return Stream.concat(userRecipes.stream(), publicRecipes.stream()).distinct().toList();
    }

    private List<RecipeIngredient> getFilteredRecipeIngredients(List<String> ingredients) {
        List<RecipeIngredient> filteredIngredients = recipeIngredientRepository.findAll();
        return filteredIngredients.stream()
                .filter(recipeIngredient -> ingredients.contains(recipeIngredient.getIngredientName())).collect(Collectors.toList());
    }

    public List<Recipe> getRecipesByUserId(Integer userId) {
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
    public Recipe addRecipe(CreateRecipeDto createRecipeDto, HttpServletRequest request) {
        String userEmail = extractUserEmailFromToken(request);

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

        recipeCreator.saveRecipeWithIngredientsAndInstructions(recipe);
        return recipe;

    }

    @Transactional
    public Recipe editRecipe(Integer id, @Validated RecipeDto recipeDto) {
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
    public void deleteRecipe(Integer id) {
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
        if(userEmail != null){
            return userRoles.contains("ROLE_ADMIN") || userEmail.equals(recipe.getUser().getEmail());
        }else{
            return false;
        }
    }

    private String extractUserEmailFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
            return claims.getSubject();
        }
        return null;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}
