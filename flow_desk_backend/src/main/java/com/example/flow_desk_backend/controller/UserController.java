package com.example.flow_desk_backend.controller;
import com.example.flow_desk_backend.dto.*;
import com.example.flow_desk_backend.security.CustomUserDetails;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping("/admin/add-employee")
    private ResponseEntity<EmployeeResponse> addEmployee(@RequestBody UserEntity newEmployee){
        EmployeeResponse savedEmployee = userService.addEmployee(newEmployee);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @PutMapping("/employee/update-employee-status/{employee_id}")
    private ResponseEntity<EmployeeResponse> updateEmployeeResponse(@PathVariable Long employee_id){
        EmployeeResponse savedEmployee = userService.updateEmployeeStatus(employee_id);

        return ResponseEntity.ok(savedEmployee);
    }

    @PostMapping("/update-password-request")
    private ResponseEntity<String> updatePasswordRequest(@RequestBody ForgotPasswordRequest request){
        return ResponseEntity.ok(userService.updatePasswordRequest(request.employeeEmail()));
    }

    @PostMapping("/validate-otp")
    private ResponseEntity<ValidateOtpResponse> validateOtp(@RequestBody ValidateOtpRequest request){
        return ResponseEntity.ok(userService.validateOtp(request.employeeEmail(), request.otp()));
    }

    @PostMapping("/updated-employee-password")
    private ResponseEntity<String> updateEmployeePassword(@RequestBody UpdatePasswordRequest request){
        return ResponseEntity.ok(userService.updateEmployeePassword(request.resetToken(), request.newPassword()));
    }

    @GetMapping("/admin/employees/all")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(){
        return ResponseEntity.ok(userService.getAllEmployees());
    }
}
