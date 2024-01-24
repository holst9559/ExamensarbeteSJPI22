package com.example.examensarbete.controller;

import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.service.IngredientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("api/v1/ingredients")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping
    public List<Ingredient> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

    @GetMapping("/{id:\\d+}")
    public Ingredient getIngredientById(@PathVariable Long id) {
        return ingredientService.getIngredientById(id);
    }

    @GetMapping("/{name:.*\\D.*}")
    public Ingredient getIngredientByName(@PathVariable String name) {
        return ingredientService.getIngredientByName(name);
    }

    @PostMapping
    public ResponseEntity<Ingredient> addIngredient(@RequestBody @Validated IngredientDto ingredientDto) {
        var created = ingredientService.addIngredient(ingredientDto);

        URI locationURI = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(locationURI).body(created);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id:\\d+}")
    public ResponseEntity<Ingredient> editIngredient(@PathVariable Long id, @RequestBody @Validated IngredientDto ingredient) {
        return ResponseEntity.ok().body(ingredientService.editIngredient(id, ingredient));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/new")
    public Ingredient[] fetchNewIngredient(@RequestParam String ingredient) {
        return ingredientService.fetchNewIngredient(ingredient);
    }

}
