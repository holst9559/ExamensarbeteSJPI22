package com.example.examensarbete.exception;

public class UserAlreadyExistException extends ResourceAlreadyExistException{
    public UserAlreadyExistException() {
        super("User already exist");
    }

    public UserAlreadyExistException(String email) {
        super("User already exist with the email :" + email);
    }
}
