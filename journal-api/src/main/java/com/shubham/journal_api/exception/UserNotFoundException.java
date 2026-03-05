package com.shubham.journal_api.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String username){
        super("user not found: " + username );
    }

    public UserNotFoundException(Long id){
        super("User not found with id: " + id);
    }
}
