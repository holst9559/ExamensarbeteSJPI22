package com.example.examensarbete.exception;

public class IngredientAlreadyExistException extends ResourceAlreadyExistException{
    public IngredientAlreadyExistException() {
        super("Ingredient already exist");
    }

    public IngredientAlreadyExistException(String name) {
        super("Ingredient already exist with the name :" + name);
    }
}
