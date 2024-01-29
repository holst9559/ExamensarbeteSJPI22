package com.example.examensarbete.utils;

import com.example.examensarbete.dto.CreateRecipeDto;
import com.example.examensarbete.dto.RecipeIngredientDto;
import com.example.examensarbete.entities.*;
import com.example.examensarbete.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
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
    private final RecipeRepository recipeRepository;

    public RecipeCreator(
            DishRepository dishRepository,
            CategoryRepository categoryRepository,
            InstructionRepository instructionRepository,
            RecipeIngredientRepository recipeIngredientRepository,
            IngredientRepository ingredientRepository,
            UnitRepository unitRepository,
            DietRepository dietRepository,
            RecipeRepository recipeRepository) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.instructionRepository = instructionRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.ingredientRepository = ingredientRepository;
        this.unitRepository = unitRepository;
        this.dietRepository = dietRepository;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public Recipe createRecipe(@Validated CreateRecipeDto createRecipeDto, User user) {
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
        if (createRecipeDto.dish().getName() != null) {
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
        if (!createRecipeDto.instructions().isEmpty()) {
            Set<Instruction> instructions = createRecipeDto.instructions().stream()
                    .map(instructionDto -> instructionRepository.save(new Instruction(instructionDto.step(), instructionDto.description())))
                    .collect(Collectors.toSet());

            recipe.setInstructions(instructions);
        }
    }

    private void setRecipeIngredients(CreateRecipeDto createRecipeDto, Recipe recipe) {
        if (!createRecipeDto.recipeIngredients().isEmpty()) {
            Set<RecipeIngredient> recipeIngredients = createRecipeDto.recipeIngredients().stream()
                    .map(ingredientDto -> {
                        var unitCheck = unitRepository.findByName(ingredientDto.unit());
                        if (unitCheck.isEmpty()) {
                            Unit newUnit = createNewUnit(ingredientDto.unit());
                            System.out.println("NEW UNIT");
                            System.out.println(newUnit);
                            RecipeIngredient newRecipeIngredient = createNewRecipeIngredient(ingredientDto, newUnit);
                            recipeIngredientRepository.save(newRecipeIngredient);
                            return newRecipeIngredient;

                        } else {
                            System.out.println("EXISTING");
                            System.out.println(unitCheck.get());
                            RecipeIngredient newRecipeIngredient = createNewRecipeIngredient(ingredientDto, unitCheck.get());
                            recipeIngredientRepository.save(newRecipeIngredient);
                            return newRecipeIngredient;
                        }
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

    private RecipeIngredient createNewRecipeIngredient(RecipeIngredientDto ingredientDto, Unit unitCheck) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        var ingredientCheck = ingredientRepository.findByName(ingredientDto.ingredient().name())
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with name: " + ingredientDto.ingredient().name()));


        recipeIngredient.setIngredient(ingredientCheck);
        recipeIngredient.setAmount(ingredientDto.amount());
        recipeIngredient.setUnit(unitCheck);
        System.out.println(unitCheck.getRecipeIngredients());
        unitCheck.getRecipeIngredients().add(recipeIngredient);

        return recipeIngredient;
    }


    private Unit createNewUnit(String name) {
        Unit unit = new Unit();
        unit.setName(name);
        return unitRepository.save(unit);
    }

    @Transactional
    public void saveRecipeWithIngredientsAndInstructions(Recipe recipe) {
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
            ingredient.setRecipe(Collections.singleton(recipe));
        }

        for (Instruction instruction : recipe.getInstructions()) {
            instruction.setRecipe(recipe);
        }

        recipeRepository.save(recipe);
    }
}
