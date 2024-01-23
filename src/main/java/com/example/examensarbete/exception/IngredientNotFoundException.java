package com.example.examensarbete.exception;

public class IngredientNotFoundException extends ResourceNotFoundException{
    public IngredientNotFoundException() {
        super("Ingredient not found");
    }

    public IngredientNotFoundException(Long id) {
        super("Ingredient not found with id: " + id);
    }

    public IngredientNotFoundException(String name){
        super("Ingredient not found with name:  " + name);
    }
}
