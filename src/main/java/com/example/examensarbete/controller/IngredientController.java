package com.example.examensarbete.controller;


import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.service.IngredientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService){
        this.ingredientService = ingredientService;
    }

    /*
    @GetMapping
    public List<Ingredient> getAllIngredients(){
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id}")
    public Ingredient getIngredientById(@PathVariable int id){
        return ingredientService.getIngredientById(id);
    }

    @GetMapping("/{name}")
    public Ingredient getIngredientByName(@PathVariable String name){
        return ingredientService.getIngredientsByName(name);
    }

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
    public String fetchNewIngredient(@RequestParam String ingredient) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String newIngredient = ingredientService.fetchNewIngredient(ingredient);
        Map<String, Object> map = objectMapper.readValue(newIngredient, new TypeReference<Map<String,Object>>(){});
        System.out.println("CONTOLLER LAYER");
        System.out.println(map.values().stream().filter(c -> c.equals(ingredient)));

        return newIngredient;
    }

}
