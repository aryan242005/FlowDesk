package com.example.flow_desk_backend.controller;

import com.example.flow_desk_backend.util.EmailSender;
import com.example.flow_desk_backend.util.RedisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flow_desk_backend.repository.CompanyRepository;
import com.example.flow_desk_backend.repository.UserRepository;
import com.example.flow_desk_backend.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.flow_desk_backend.dto.LoginRequest;
import com.example.flow_desk_backend.dto.LoginResponse;
import com.example.flow_desk_backend.dto.SignupResponse;
import com.example.flow_desk_backend.entity.CompanyEntity;
import com.example.flow_desk_backend.entity.Role;
import com.example.flow_desk_backend.entity.UserEntity;
import com.example.flow_desk_backend.util.JwtUtil;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailSender emailSender;
    private final RedisService redisService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signUp(@Valid @RequestBody UserEntity user) {
        CompanyEntity company = companyRepository.save(user.getCompany());
        CompanyEntity savedCompany = companyRepository.save(company);

        user.setCompany(savedCompany);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.COMPANY_ADMIN);
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);

        SignupResponse response = new SignupResponse(
                savedUser.getId(),
                savedUser.getEmployeeEmail(),
                savedUser.getRole().name(),
                "Company Registered Successfully"
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        UserEntity user = ((CustomUserDetails) auth.getPrincipal()).getUser();

        String token = jwtUtil.generateToken(user.getEmployeeEmail());

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmployeeEmail(),
                user.getRole().name(),
                token,
                "Login successful"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/customer-request")
    public ResponseEntity<String> requestCustomerToken(@RequestBody String email){
        String customerToken = UUID.randomUUID().toString();
        redisService.set(customerToken, email, Duration.ofMinutes(30));

        emailSender.sendEmail(email, "Request for Access Token", customerToken);

        return ResponseEntity.ok("Access token sent to " + email);
    }
}
