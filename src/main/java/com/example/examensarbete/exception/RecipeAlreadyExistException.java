package com.example.examensarbete.exception;

public class RecipeAlreadyExistException extends ResourceAlreadyExistException{
    public RecipeAlreadyExistException() {
        super("Recipe already exist");
    }

    public RecipeAlreadyExistException(String title) {
        super("Recipe already exist with the title :" + title);
    }
}
