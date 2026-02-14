package com.example.flow_desk_backend.exception;

public class CompanyNotFoundException extends RuntimeException{
    public CompanyNotFoundException(String message){
        super(message);
    }
}
