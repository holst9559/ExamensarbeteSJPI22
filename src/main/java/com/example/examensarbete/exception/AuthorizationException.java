package com.example.examensarbete.exception;

public class AuthorizationException extends IllegalAccessException{

    public AuthorizationException(){
        super("Not allowed");
    }
}
