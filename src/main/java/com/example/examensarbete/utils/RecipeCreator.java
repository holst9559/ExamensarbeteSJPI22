package com.example.examensarbete.utils;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RecipeCreator {
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final InstructionRepository instructionRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;
    private final UnitRepository unitRepository;
    private final DietRepository dietRepository;

    public RecipeCreator(
            DishRepository dishRepository,
            CategoryRepository categoryRepository,
            InstructionRepository instructionRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository,
            UnitRepository unitRepository,
            DietRepository dietRepository) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.instructionRepository = instructionRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.unitRepository = unitRepository;
        this.dietRepository = dietRepository;
    }

    public Recipe createRecipe(CreateRecipeDto createRecipeDto, User user) {
        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.setTitle(createRecipeDto.title());
        recipe.setDescription(createRecipeDto.description());
        recipe.setPrepTime(createRecipeDto.prepTime());
        recipe.setCookTime(createRecipeDto.cookTime());
        recipe.setServings(createRecipeDto.servings());
        recipe.setVisible(createRecipeDto.visible());
        recipe.setImgUrl(createRecipeDto.imgUrl());

        setDish(createRecipeDto, recipe);
        setCategory(createRecipeDto, recipe);
        setInstructions(createRecipeDto, recipe);
        setRecipeIngredients(createRecipeDto, recipe);
        setDiet(createRecipeDto, recipe);

        return recipe;
    }

    private void setDish(CreateRecipeDto createRecipeDto, Recipe recipe) {
        if (createRecipeDto.dish() != null) {
            Dish dish = dishRepository.findById(createRecipeDto.dish().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Dish not found with id: " + createRecipeDto.dish().getId()));
            recipe.setDish(dish);
        }
    }

    private void setCategory(CreateRecipeDto createRecipeDto, Recipe recipe) {
        if (createRecipeDto.category().getId() != null) {
            Category category = categoryRepository.findById(createRecipeDto.category().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + createRecipeDto.category().getId()));
            recipe.setCategory(category);
        }
    }

    private void setInstructions(CreateRecipeDto createRecipeDto, Recipe recipe) {
        if (createRecipeDto.instructions() != null && !createRecipeDto.instructions().isEmpty()) {
            Set<Instruction> instructions = createRecipeDto.instructions().stream()
                    .map(instructionDto -> instructionRepository.save(new Instruction(instructionDto.step(), instructionDto.description())))
                    .collect(Collectors.toSet());

            recipe.setInstructions(instructions);
        }
    }

    private void setRecipeIngredients(CreateRecipeDto createRecipeDto, Recipe recipe) {
        if (createRecipeDto.recipeIngredients() != null && !createRecipeDto.recipeIngredients().isEmpty()) {
            Set<RecipeIngredient> recipeIngredients = createRecipeDto.recipeIngredients().stream()
                    .map(ingredientDto -> {
                        var recipeIngredientCheck = recipeIngredientRepository.findByNameUnitAmount(createRecipeDto.recipeIngredients());
                        if (recipeIngredientCheck.isPresent()) {
                            return recipeIngredientCheck.get();
                        }
                        RecipeIngredient newRecipeIngredient = createNewRecipeIngredient(ingredientDto);
                        recipeIngredientRepository.save(newRecipeIngredient);
                        return newRecipeIngredient;

                    })
                    .collect(Collectors.toSet());
            recipe.setRecipeIngredients(recipeIngredients);
        }
    }

    private void setDiet(CreateRecipeDto createRecipeDto, Recipe recipe) {
        if (createRecipeDto.diet().getId() != null) {
            Diet diet = dietRepository.findById(createRecipeDto.diet().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Diet not found with id: " + createRecipeDto.diet().getId()));
            recipe.setDiet(diet);
        }
    }

    private RecipeIngredient createNewRecipeIngredient(RecipeIngredientDto ingredientDto) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        var ingredientCheck = ingredientRepository.findByName(ingredientDto.ingredientName())
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with name: " + ingredientDto.ingredientName()));
        var unitCheck = unitRepository.findByName(ingredientDto.unit())
                .orElseThrow(() -> new EntityNotFoundException("Unit not found with name: " + ingredientDto.unit()));

        recipeIngredient.setIngredient(ingredientCheck);
        recipeIngredient.setAmount(ingredientDto.amount());
        recipeIngredient.setUnit(unitCheck);

        return recipeIngredient;
    }
}
