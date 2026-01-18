package com.example.flow_desk_backend.service;

import com.example.flow_desk_backend.dto.ValidateOtpResponse;
import com.example.flow_desk_backend.exception.UserNotFoundException;
import com.example.flow_desk_backend.util.EmailSender;
import com.example.flow_desk_backend.util.OtpService;
import com.example.flow_desk_backend.util.RedisService;
import jakarta.validation.constraints.Email;
import org.apache.catalina.User;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final RedisService redisService;

    public EmployeeResponse addEmployee(UserEntity newEmployee) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) auth.getPrincipal();
        UserEntity loggedInUser = principal.getUser();

        newEmployee.setCompany(loggedInUser.getCompany());
        newEmployee.setPassword(passwordEncoder.encode(newEmployee.getPassword()));
        newEmployee.setRole(Role.EMPLOYEE);
        newEmployee.setIsActive(true);

        UserEntity savedEmployee = userRepository.save(newEmployee);

        return mapToDto(savedEmployee);
    }

    public EmployeeResponse updateEmployeeStatus(Long employee_id){
        UserEntity employee = userRepository.findById(employee_id).orElseThrow(() -> new UserNotFoundException("User not found"));;
        employee.setIsActive(!employee.getIsActive());
        UserEntity savedEmployee = userRepository.save(employee);

        return mapToDto(savedEmployee);
    }

    public String updatePasswordRequest(String employeeEmail){
        UserEntity employee = userRepository.findByEmployeeEmail(employeeEmail).orElseThrow(() -> new UserNotFoundException("User not found with email : " + employeeEmail));

        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        String hashedOtp = passwordEncoder.encode(otp);
        emailSender.sendEmail(employeeEmail, "Password change", otp);
        final Duration OTP_TTL = Duration.ofMinutes(5);
        redisService.set("FORGOT_PWD_OTP:"+employeeEmail, hashedOtp, OTP_TTL);

        return "OTP Send Successfully";
    }

    public ValidateOtpResponse validateOtp(String employeeEmail, String otp){
        String hashedOtp = redisService.get("FORGOT_PWD_OTP:"+employeeEmail);

        if(hashedOtp == null){
            return new ValidateOtpResponse("INVALID", null);
        }

        if(!passwordEncoder.matches(otp, hashedOtp)){
            return new ValidateOtpResponse("INVALID", null);
        }

        String resetToken = UUID.randomUUID().toString();
        redisService.set(resetToken, employeeEmail, Duration.ofMinutes(5));
        redisService.delete("FORGOT_PWD_OTP:"+employeeEmail);

        return new ValidateOtpResponse("VALID", resetToken);
    }

    public String updateEmployeePassword(String resetToken, String newPassword){
        String employeeEmail = redisService.get(resetToken);

        if(employeeEmail == null){
            return "Not authorized to update password";
        }

        UserEntity employee = userRepository.findByEmployeeEmail(employeeEmail).orElseThrow(() -> new UserNotFoundException("User not found"));

        employee.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(employee);
        redisService.delete(resetToken);

        return "Password Updated Successfully";
    }

    public List<EmployeeResponse> getAllEmployees(){
        List<UserEntity> employees = userRepository.findAll();

        return employees.stream().map(this::mapToDto).toList();
    }

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
}
