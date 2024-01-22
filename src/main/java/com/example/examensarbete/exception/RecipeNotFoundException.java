package com.example.examensarbete.exception;

public class RecipeNotFoundException extends ResourceNotFoundException{
    public RecipeNotFoundException() {
        super("Recipe not found");
    }

    public RecipeNotFoundException(Long id) {
        super("Recipe not found with id: " + id);
    }

    public RecipeNotFoundException(String title){
        super("Recipe not found with name:  " + title);
    }
}
