package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.ValidateOtpResponse;
import com.example.flow_desk_backend.entity.ProjectEntity;
import com.example.flow_desk_backend.exception.AuthorizationException;
import com.example.flow_desk_backend.exception.InvalidOtpException;
import com.example.flow_desk_backend.exception.ProjectNotFoundException;
import com.example.flow_desk_backend.exception.UserNotFoundException;
import com.example.flow_desk_backend.repository.ProjectRepository;
import com.example.flow_desk_backend.util.EmailSender;
import com.example.flow_desk_backend.util.RedisService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.flow_desk_backend.dto.EmployeeResponse;
import com.example.flow_desk_backend.entity.Role;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.repository.UserRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final RedisService redisService;

    // Add developer or tester
    public EmployeeResponse addMemberEmployee(UserEntity newEmployee) {
        // Check if new member isn't manager
        if(newEmployee.getRole().equals(Role.MANAGER)) {
            throw new AuthorizationException("Only admin can add a manager level member");
        }

        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Set new member's details
        newEmployee.setCompany(loggedInUser.getCompany());
        newEmployee.setPassword(passwordEncoder.encode(newEmployee.getPassword()));
        newEmployee.setIsActive(true);

        // Save new employee
        UserEntity savedEmployee = userRepository.save(newEmployee);

        // Return saved member
        return mapToDto(savedEmployee);
    }

    // Add manager level member
    public EmployeeResponse addManagerEmployee(UserEntity newEmployee) {
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Check if current user is admin
        if(!loggedInUser.getRole().equals(Role.COMPANY_ADMIN)) {
            throw new AuthorizationException("Only admin can add a manager level member");
        }

        // Set new member details
        newEmployee.setCompany(loggedInUser.getCompany());
        newEmployee.setPassword(passwordEncoder.encode(newEmployee.getPassword()));
        newEmployee.setIsActive(true);

        // Save new member
        UserEntity savedEmployee = userRepository.save(newEmployee);

        // Map and return new member
        return mapToDto(savedEmployee);
    }

    public EmployeeResponse updateEmployeeStatus(Long employeeId){
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        UserEntity employee = userRepository.findById(employeeId).orElseThrow(() -> new UserNotFoundException("User not found"));;

        // Check if current user and employee to update belong to same company
        if(!loggedInUser.getCompany().getCompanyId().equals(employee.getCompany().getCompanyId())){
            throw new AuthorizationException("Cannot update status of another company's employee");
        }

        // Change status and save
        employee.setIsActive(!employee.getIsActive());
        UserEntity savedEmployee = userRepository.save(employee);

        return mapToDto(savedEmployee);
    }

    // Update password request when user click's 'forgot password', otp is sent to user
    public void updatePasswordRequest(String employeeEmail){
        // Check employee exists or not
        UserEntity employee = userRepository.findByEmployeeEmail(employeeEmail).orElseThrow(() -> new UserNotFoundException("User not found with email : " + employeeEmail));

        // Generate OTP
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        // Hash OTP and send non-hashed OTP through email
        String hashedOtp = passwordEncoder.encode(otp);
        emailSender.sendEmail(employeeEmail, "Password change", otp);

        // Set TTL of OTP and store in redis
        final Duration OTP_TTL = Duration.ofMinutes(5);
        redisService.set("FORGOT_PWD_OTP:"+employeeEmail, hashedOtp, OTP_TTL);
    }

    // Verifies OTP and sends a resetToken to reset password
    public ValidateOtpResponse validateOtp(String employeeEmail, String otp){
        // Fetch stored OTP
        String hashedOtp = redisService.get("FORGOT_PWD_OTP:"+employeeEmail);

        if(hashedOtp == null){
            throw new InvalidOtpException("Invalid OTP");
        }

        // Validate OTP
        if(!passwordEncoder.matches(otp, hashedOtp)){
            throw new InvalidOtpException("Invalid OTP");
        }

        // Generate resetToken, set it as key, and employee email as value
        String resetToken = UUID.randomUUID().toString();

        // Set TTL and store in redis
        redisService.set(resetToken, employeeEmail, Duration.ofMinutes(5));

        // Delete OTP from redis after validation
        redisService.delete("FORGOT_PWD_OTP:" + employeeEmail);

        return new ValidateOtpResponse(resetToken);
    }

    // User sends new password and reset token
    public String updateEmployeePassword(String resetToken, String newPassword){
        // Fetch email through resetToken
        String employeeEmail = redisService.get(resetToken);

        if(employeeEmail == null){
            throw new AuthorizationException("Invalid reset token");
        }

        // Fetch employee with email
        UserEntity employee = userRepository.findByEmployeeEmail(employeeEmail).orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update password for the employee found
        employee.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(employee);
        redisService.delete(resetToken);

        return "Password Updated Successfully";
    }

    public List<EmployeeResponse> getAllMemberEmployees(){
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Fetch members by company and roles
        List<Role> roles = List.of(Role.DEVELOPER, Role.TESTER);
        List<UserEntity> employees = userRepository.findByCompanyAndRoleIn(loggedInUser.getCompany(), roles);

        return employees.stream().map(this::mapToDto).toList();
    }

    public List<EmployeeResponse> getAllEmployees(){
        // Fetch current user
        UserEntity loggedInUser = getLoggedInUser();

        // Fetch members by company
        List<UserEntity> employees = userRepository.findByCompany(loggedInUser.getCompany());

        return employees.stream().map(this::mapToDto).toList();
    }

    // Utility Functions

    public EmployeeResponse mapToDto(UserEntity employee){
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeName(),
                employee.getEmployeeEmail(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getPhoneNumber(),
                employee.getRole(),
                employee.getIsActive(),
                employee.getCreatedAt()
        );
    }

    public UserEntity getLoggedInUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        return principal.getUser();
    }
}
