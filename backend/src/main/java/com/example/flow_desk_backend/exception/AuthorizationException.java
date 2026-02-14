package com.example.flow_desk_backend.exception;

public class AuthorizationException extends RuntimeException{
    public AuthorizationException(String message){
        super(message);
    }
}
