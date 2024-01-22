package com.example.examensarbete.service;

import com.example.examensarbete.data.IngredientResponse;
import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.exception.IngredientAlreadyExistException;
import com.example.examensarbete.exception.IngredientApiException;
import com.example.examensarbete.exception.IngredientNotFoundException;
import com.example.examensarbete.exception.ResourceNotFoundException;
import com.example.examensarbete.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

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
        var ingredientCheck = ingredientRepository.findById(id);
        if (ingredientCheck.isPresent()) {
            return ingredientCheck.get();
        }
        throw new IngredientNotFoundException(id);
    }

    public Ingredient getIngredientByName(String name) {
        var ingredientCheck = ingredientRepository.findByName(name);
        if (ingredientCheck.isPresent()) {
            return ingredientCheck.get();
        }
        throw new IngredientNotFoundException(name);
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
        throw new IngredientAlreadyExistException(ingredientDto.name());
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
            throw new IngredientNotFoundException(id);
        }
    }

    @Transactional
    public void deleteIngredient(Long id) {
        var ingredientToDelete = ingredientRepository.findById(id);
        if (ingredientToDelete.isEmpty()) {
            throw new IngredientNotFoundException(id);
        }
        ingredientRepository.deleteById(id);
    }

    public Ingredient[] fetchNewIngredient(String ingredient) {
        try {
            IngredientResponse response = webClient.get().uri("/food/ingredients/search?query=" + ingredient)
                    .header("X-API-KEY", apiKey)
                    .retrieve().bodyToMono(IngredientResponse.class)
                    .block();

            if (response != null) {
                return response.getResults();
            } else {
                throw new IllegalStateException("Response is null");
            }
        } catch (WebClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            String statusText = ex.getStatusText();
            byte[] responseBody = ex.getResponseBodyAsByteArray();
            throw new IngredientApiException(statusCode, statusText, responseBody);
        }
    }
}
