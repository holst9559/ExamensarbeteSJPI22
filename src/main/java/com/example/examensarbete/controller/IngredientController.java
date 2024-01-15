package com.example.examensarbete.controller;

import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("api/v1/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService){
        this.ingredientService = ingredientService;
    }



    @GetMapping
    public List<Ingredient> getAllIngredients(){
        return ingredientService.getAllIngredients();
    }


    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable Long id){
        return ingredientService.getIngredientById(id);
    }

    @GetMapping("/{name}")
    public Ingredient getIngredientByName(@PathVariable String name){
        return ingredientService.getIngredientByName(name);
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addIngredient(@RequestBody @Validated IngredientDto ingredientDto){
        ingredientService.addIngredient(ingredientDto);
    }

    /*

    @PatchMapping("/{id}")
    public Ingredient editIngredient(@PathVariable int id, @RequestBody @Validated IngredientDto ingredient){
        return ingredientService.editIngredient(id, ingredient);
    }

    @DeleteMapping("/{id}")
    public String deleteIngredient(@PathVariable int id){
        ingredientService.deleteIngredient(id);
        return "Ingredient with id: " + id + " was deleted.";
    }

     */

    @GetMapping("/new")
    public Ingredient[] fetchNewIngredient(@RequestParam String ingredient) {
        return ingredientService.fetchNewIngredient(ingredient);
    }

}
