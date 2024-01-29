package com.example.examensarbete.service;

import com.example.examensarbete.data.IngredientResponse;
import com.example.examensarbete.dto.IngredientDto;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.exception.IngredientAlreadyExistException;
import com.example.examensarbete.exception.IngredientApiException;
import com.example.examensarbete.exception.IngredientNotFoundException;
import com.example.examensarbete.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class IngredientService {
    private static final Logger logger = LoggerFactory.getLogger(IngredientService.class);
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

    public Ingredient getIngredientById(Integer id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ingredient not found with id: '{}'", id);
                    return new IngredientNotFoundException(id);
                });
    }

    public Ingredient getIngredientByName(String name) {
        return ingredientRepository.findByName(name)
                .orElseThrow(() -> {
                    logger.error("Ingredient not found with name: '{}'", name);
                    return new IngredientNotFoundException(name);
                });
    }

    @Transactional
    public Ingredient addIngredient(@Validated IngredientDto ingredientDto) {
        ingredientRepository.findByName(ingredientDto.name())
                .ifPresent(existingIngredient -> {
                    logger.error("Ingredient already exists with name: '{}'", ingredientDto.name());
                    throw new IngredientAlreadyExistException(ingredientDto.name());
                });

        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientDto.id());
        ingredient.setName(ingredientDto.name());
        return ingredientRepository.save(ingredient);
    }


    @Transactional
    public Ingredient editIngredient(Integer id, @Validated IngredientDto ingredientDto) {
        return ingredientRepository.findById(id)
                .map(ingredientToUpdate -> {
                    ingredientToUpdate.setId(ingredientDto.id());
                    ingredientToUpdate.setName(ingredientDto.name());
                    return ingredientRepository.save(ingredientToUpdate);
                })
                .orElseThrow(() -> {
                    logger.error("Ingredient not found with id: '{}'", id);
                    return new IngredientNotFoundException(id);
                });
    }

    @Transactional
    public void deleteIngredient(Integer id) {
        ingredientRepository.findById(id)
                .ifPresentOrElse(ingredientRepository::delete,
                        () -> {
                            logger.error("Ingredient not found with id: '{}'", id);
                            throw new IngredientNotFoundException(id);
                        });
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
                logger.debug("Response from external API is null");
                throw new IllegalStateException("Response is null");
            }
        } catch (WebClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            String statusText = ex.getStatusText();
            byte[] responseBody = ex.getResponseBodyAsByteArray();

            logger.error("Failed to fetch data from Spoonacular. Status Code: {}, Status Text: {}", statusCode, statusText);
            logger.debug("Response Body: {}", new String(responseBody, StandardCharsets.UTF_8));

            throw new IngredientApiException(statusCode, statusText, responseBody);
        }
    }
}
