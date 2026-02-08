package com.example.flow_desk_backend.controller;
import com.example.flow_desk_backend.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/manager/add-employee")
    public ResponseEntity<EmployeeResponse> addMemberEmployee(@RequestBody CreateEmployeeRequest request){
        EmployeeResponse savedEmployee = userService.addMemberEmployee(request.employee());

        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @PostMapping("/admin/add-employee")
    public ResponseEntity<EmployeeResponse> addManagerEmployee(@RequestBody UserEntity newEmployee){
        EmployeeResponse savedEmployee = userService.addManagerEmployee(newEmployee);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
    }

    @PutMapping("/manager/update-employee-status/{employeeId}")
    public ResponseEntity<EmployeeResponse> updateEmployeeStatus(@PathVariable Long employeeId){
        EmployeeResponse savedEmployee = userService.updateEmployeeStatus(employeeId);

        return ResponseEntity.ok(savedEmployee);
    }

    @PostMapping("/update-password-request")
    public ResponseEntity<String> updatePasswordRequest(@RequestBody ForgotPasswordRequest request){
        return ResponseEntity.ok(userService.updatePasswordRequest(request.employeeEmail()));
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<ValidateOtpResponse> validateOtp(@RequestBody ValidateOtpRequest request){
        return ResponseEntity.ok(userService.validateOtp(request.employeeEmail(), request.otp()));
    }

    @PostMapping("/update-employee-password")
    public ResponseEntity<String> updateEmployeePassword(@RequestBody UpdatePasswordRequest request){
        return ResponseEntity.ok(userService.updateEmployeePassword(request.resetToken(), request.newPassword()));
    }

    @GetMapping("/manager/project/{projectId}/employees")
    public ResponseEntity<List<EmployeeResponse>> getAllMembersByProject(@PathVariable Long projectId){
        return ResponseEntity.ok(userService.getAllMemberByProject(projectId));
    }

    @GetMapping("/admin/employees/members")
    public ResponseEntity<List<EmployeeResponse>> getAllMemberEmployees(){
        return ResponseEntity.ok(userService.getAllMemberEmployees());
    }

    @GetMapping("/admin/employees/all")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(){
        return ResponseEntity.ok(userService.getAllEmployees());
    }
}
