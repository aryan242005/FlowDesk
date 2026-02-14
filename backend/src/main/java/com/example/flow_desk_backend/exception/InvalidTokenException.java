package com.example.flow_desk_backend.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(){
        super("Invalid or expired access token");
    }
}
