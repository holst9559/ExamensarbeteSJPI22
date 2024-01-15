package com.example.examensarbete.service;

import com.example.examensarbete.data.IngredientResponse;
import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.repository.IngredientRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.var;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class IngredientService {
    @Value("${app.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final IngredientRepository ingredientRepository;

    public IngredientService(WebClient.Builder webClientBuilder, IngredientRepository ingredientRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.spoonacular.com").codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)).build();
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
    public void addIngredient(@Validated IngredientDto ingredientDto) {
        var ingredientCheck = ingredientRepository.findByName(ingredientDto.name());
        if(ingredientCheck.isEmpty()){
            Ingredient ingredient = new Ingredient();
            ingredient.setId(ingredientDto.id());
            ingredient.setName(ingredientDto.name());
            ingredientRepository.save(ingredient);
        }
        throw new IllegalArgumentException("Ingredient with the name :" + ingredientDto.name() + " already exist.");
    }


    public Ingredient[] fetchNewIngredient(String ingredient) {
        IngredientResponse response = webClient.get().uri("/food/ingredients/search?query=" + ingredient).header("X-API-KEY", apiKey).retrieve().bodyToMono(IngredientResponse.class).block();

        assert response != null;
        return response.getResults();
    }
}
