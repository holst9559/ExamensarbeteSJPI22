package com.example.examensarbete.data;

import com.example.examensarbete.entities.Ingredient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IngredientResponse {

    private Ingredient[] results;
    private int offset;
    private int number;
    private int totalResults;

    public IngredientResponse(Ingredient[] results, int offset, int number, int totalResults) {
        this.results = results;
        this.offset = offset;
        this.number = number;
        this.totalResults = totalResults;
    }

    @Override
    public String toString() {
        return "IngredientResponse{" +
                "results=" + Arrays.toString(results) +
                ", offset=" + offset +
                ", number=" + number +
                ", totalResults=" + totalResults +
                '}';
    }
}
