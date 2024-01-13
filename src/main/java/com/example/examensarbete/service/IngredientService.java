package com.example.examensarbete.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IngredientService {

    private final WebClient webClient;

    public IngredientService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("https://api.nal.usda.gov/fdc/v1").build();
    }

    public String fetchNewIngredient(String ingredient){
        return webClient.get()
                .uri("/foods/search?query=" + ingredient)
                .header("X-API-KEY", "eiOOXHBRf82BoLFdAY21Q6gFB20SV1IhEuifUtgT")
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
