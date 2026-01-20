package com.example.flow_desk_backend.exception;

public class EmployeeNotAssignedException extends RuntimeException{
    public EmployeeNotAssignedException (String message){
        super(message);
    }
}
