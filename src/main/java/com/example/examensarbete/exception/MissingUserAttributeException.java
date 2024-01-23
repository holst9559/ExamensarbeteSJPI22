package com.example.examensarbete.exception;

public class MissingUserAttributeException extends IllegalArgumentException{
    public MissingUserAttributeException(){
        super("Invalid or missing attributes");
    }

    public MissingUserAttributeException(String name){
        super("Invalid or missing attributes for user: " + name);
    }

}
