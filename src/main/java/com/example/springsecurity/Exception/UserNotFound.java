package com.example.springsecurity.Exception;

import com.example.springsecurity.Model.User;

public class UserNotFound extends RuntimeException{
    public UserNotFound(String message){
        super(message);
    }
}
