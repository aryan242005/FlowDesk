package com.example.flow_desk_backend.exception;

public class InformationMissingException extends RuntimeException{
    public InformationMissingException(String message){
        super(message);
    }
}
