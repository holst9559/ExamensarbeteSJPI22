package com.example.examensarbete.service;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.exception.RecipeAlreadyExistException;
import com.example.examensarbete.exception.RecipeNotFoundException;
import com.example.examensarbete.exception.UserNotFoundException;
import com.example.examensarbete.repository.*;
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

import java.security.Key;
import java.util.*;
import java.util.stream.Stream;

@Service
public class RecipeService {
    private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final RecipeCreator recipeCreator;
    @Value("${JWT_SECRET}")
    private String SECRET_KEY;

    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository,
                         RecipeCreator recipeCreator) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
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

    public List<Recipe> getAllPublicRecipes(HttpServletRequest request) {
        Map<String, Object> userDetails = extractUserDetailsFromToken(request);
        String email = (String) userDetails.get("email");

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
                    logger.error("Recipe not found with id: '{}'", id);
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
                    logger.error("Recipe not found with title: '{}'", title);
                    return new RecipeNotFoundException(title);
                });
    }

    public List<Recipe> getRecipesByUserId(Integer userId, HttpServletRequest request) {
        Map<String, Object> userDetails = extractUserDetailsFromToken(request);
        String email = (String) userDetails.get("email");
        List<String> roles = (List<String>) userDetails.get("roles");

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if (roles.contains("ROLE_ADMIN") || userId.equals(user.getId())) {
            logger.info("Returned all recipes from userId: '{}'", userId);
            return recipeRepository.findByUserId(userId);
        } else {
            return recipeRepository.findByVisibleAndUserId(true, userId);
        }
    }

    @Transactional
    public Recipe addRecipe(CreateRecipeDto createRecipeDto, HttpServletRequest request) {
        Map<String, Object> userDetails = extractUserDetailsFromToken(request);
        String email = (String) userDetails.get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

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
    public Recipe editRecipe(Integer id, RecipeDto recipeDto, HttpServletRequest request) {
        var recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with id: '{}", id);
                    return new RecipeNotFoundException(id);
                });

        if (isUserAuthorized(recipe, request)) {
            Recipe updatedRecipe = updateRecipe(recipe, recipeDto);
            logger.info("Recipe updated with id: '{}'", id);
            return recipeRepository.save(updatedRecipe);
        } else {
            logger.error("User not authorized to edit recipe with id: '{}'", id);
            throw new RecipeNotFoundException(id);
        }
    }

    @Transactional
    public void deleteRecipe(Integer id, HttpServletRequest request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Recipe not found with id: '{}", id);
                    return new RecipeNotFoundException(id);
                });

        if (isUserAuthorized(recipe, request)) {
            try {
                recipeRepository.deleteById(id);
                logger.info("Recipe deleted with id: '{}'", id);
            } catch (Exception e) {
                logger.error("Failed to delete recipe with id: '{}'", id, e);
            }
        } else {
            logger.error("User not authorized to delete recipe with id: '{}'", id);
            throw new RecipeNotFoundException(id);
        }
    }

    private boolean isUserAuthorized(Recipe recipe, HttpServletRequest request) {
        Map<String, Object> userDetails = extractUserDetailsFromToken(request);
        String email = (String) userDetails.get("email");
        List<String> roles = (List<String>) userDetails.get("roles");
        if (email != null) {
            return roles.contains("ROLE_ADMIN") || email.equals(recipe.getUser().getEmail());
        } else {
            return false;
        }
    }

    private Map<String, Object> extractUserDetailsFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();

            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("email", claims.getSubject());
            userDetails.put("roles", claims.get("scopes", List.class));

            return userDetails;
        }
        return new HashMap<>();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}
