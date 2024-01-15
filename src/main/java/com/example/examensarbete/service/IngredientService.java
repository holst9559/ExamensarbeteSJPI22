package com.example.examensarbete.service;

import com.example.examensarbete.data.IngredientResponse;
import com.example.examensarbete.entities.Ingredient;
import com.example.examensarbete.repository.IngredientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class IngredientService {
    @Value("${app.api.key}")
    private String apiKey;
    private final WebClient webClient;
    private final IngredientRepository ingredientRepository;

    public IngredientService(WebClient.Builder webClientBuilder, IngredientRepository ingredientRepository){
        this.webClient = webClientBuilder.baseUrl("https://api.spoonacular.com")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Integer id){
        return ingredientRepository.findById(id).orElseThrow(RuntimeException::new);
    }




    public Ingredient[] fetchNewIngredient(String ingredient){
        IngredientResponse response = webClient.get()
                .uri("/food/ingredients/search?query=" + ingredient)
                .header("X-API-KEY", apiKey)
                .retrieve()
                .bodyToMono(IngredientResponse.class)
                .block();

        assert response != null;
        return response.getResults();
    }
}
