package com.example.examensarbete.service;

import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class IngredientService {
    @Value("${app.api.key}")
    private String apiKey;
    private final WebClient webClient;

    public IngredientService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("https://api.nal.usda.gov/fdc/v1")
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    public String fetchNewIngredient(String ingredient){
        return webClient.get()
                .uri("/foods/search?query=" + ingredient +"&pageSize=1&dataType=Foundation")
                .header("X-API-KEY", apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
