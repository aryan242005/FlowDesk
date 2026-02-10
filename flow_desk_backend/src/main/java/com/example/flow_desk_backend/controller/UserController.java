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
    public ResponseEntity<ApiResponse<EmployeeResponse>> addMemberEmployee(@RequestBody CreateEmployeeRequest request){
        ApiResponse<EmployeeResponse> response = new ApiResponse<>(
                true,
                "Employee created successfully",
                userService.addMemberEmployee(request.employee())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/admin/add-employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> addManagerEmployee(@RequestBody UserEntity newEmployee){
        ApiResponse<EmployeeResponse> response = new ApiResponse<>(
                true,
                "Employee created successfully",
                userService.addManagerEmployee(newEmployee)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/manager/update-employee-status/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployeeStatus(@PathVariable Long employeeId){
        EmployeeResponse employee = userService.updateEmployeeStatus(employeeId);
        String status = employee.isActive() ? "active" : "inactive";
        ApiResponse<EmployeeResponse> response = new ApiResponse<>(
                true,
                "Employee with id " + employeeId + " is " + status,
                employee
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/update-password-request")
    public ResponseEntity<ApiResponse<String>> updatePasswordRequest(@RequestBody ForgotPasswordRequest request){
        userService.updatePasswordRequest(request.employeeEmail());
        ApiResponse<String> response = new ApiResponse<>(true, "OTP Sent Successfully", null);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<ApiResponse<ValidateOtpResponse>> validateOtp(@RequestBody ValidateOtpRequest request){
        ApiResponse<ValidateOtpResponse> response = new ApiResponse<>(
                true,
                "OTP is Valid",
                userService.validateOtp(request.employeeEmail(), request.otp())
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
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
