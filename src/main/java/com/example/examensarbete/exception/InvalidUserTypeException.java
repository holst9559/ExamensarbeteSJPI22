package com.example.examensarbete.exception;

public class InvalidUserTypeException extends IllegalArgumentException{
    public InvalidUserTypeException(){
        super("Invalid or missing attributes");
    }

    public InvalidUserTypeException(String name){
        super("Invalid or missing attributes for user: " + name);
    }

}
