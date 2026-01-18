package com.example.flow_desk_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.flow_desk_backend.entity.CustomerEntity;
import com.example.flow_desk_backend.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CustomerController {
    
    private final CustomerService customerservice;

    @PostMapping("/employee/add-customer")
    public ResponseEntity<CustomerEntity> addCustomer(@RequestBody CustomerEntity newCustomer){
        CustomerEntity savedCustomer = customerservice.addCustomer(newCustomer);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }
}
