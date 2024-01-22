package com.example.examensarbete.exception;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(){
        super("User not found");
    }
    public UserNotFoundException(Long id){
        super("User not found with id: " + id);
    }
}
