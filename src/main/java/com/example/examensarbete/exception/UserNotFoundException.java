package com.example.examensarbete.exception;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(){
        super("User not found");
    }
    public UserNotFoundException(Integer id){
        super("User not found with id: " + id);
    }
    public UserNotFoundException(String email){
        super("User not found with email: " + email);
    }
}
