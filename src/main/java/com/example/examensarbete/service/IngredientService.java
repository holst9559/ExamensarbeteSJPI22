package com.example.examensarbete.service;

import com.example.examensarbete.data.IngredientResponse;
import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;


@Service
public class IngredientService {
    private final WebClient webClient;
    private final IngredientRepository ingredientRepository;
    @Value("${app.api.key}")
    private String apiKey;

    public IngredientService(WebClient.Builder webClientBuilder, IngredientRepository ingredientRepository) {
        this.webClient = webClientBuilder.build();
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Long id) {
        return ingredientRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    public Ingredient getIngredientByName(String name) {
        return ingredientRepository.findByName(name).orElseThrow(RuntimeException::new);
    }

    @Transactional
    public Ingredient addIngredient(@Validated IngredientDto ingredientDto) {
        var ingredientCheck = ingredientRepository.findByName(ingredientDto.name());
        if (ingredientCheck.isEmpty()) {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(ingredientDto.id());
            ingredient.setName(ingredientDto.name());
            return ingredientRepository.save(ingredient);
        }
        throw new IllegalArgumentException("Ingredient with the name : " + ingredientDto.name() + " already exist.");
    }

    @Transactional
    public Ingredient editIngredient(Long id, @Validated IngredientDto ingredientDto) {
        var ingredientCheck = ingredientRepository.findById(id);

        if (ingredientCheck.isPresent()) {
            Ingredient ingredientToUpdate = ingredientCheck.get();
            ingredientToUpdate.setId(ingredientDto.id());
            ingredientToUpdate.setName(ingredientDto.name());

            return ingredientRepository.save(ingredientToUpdate);
        } else {
            throw new RuntimeException("Ingredient with the id: " + id + " was not found");
        }
    }

    @Transactional
    public void deleteIngredient(Long id) {
        var ingredientToDelete = ingredientRepository.findById(id);
        if (ingredientToDelete.isEmpty()) {
            throw new RuntimeException("Ingredient with the ID: " + id + " was not found.");
        }
        ingredientRepository.deleteById(id);
    }

    public Ingredient[] fetchNewIngredient(String ingredient) {
        IngredientResponse response = webClient.get().uri("/food/ingredients/search?query=" + ingredient)
                .header("X-API-KEY", apiKey)
                .retrieve().bodyToMono(IngredientResponse.class)
                .block();

        assert response != null;
        return response.getResults();
    }
}
