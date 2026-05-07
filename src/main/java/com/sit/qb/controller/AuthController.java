package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.AdminRegisterDto;
import com.sit.qb.dtos.AgentRegisterDto;
import com.sit.qb.dtos.AuthResponseDto;
import com.sit.qb.dtos.CustomerRegisterDto;
import com.sit.qb.dtos.LoginRequestDto;
import com.sit.qb.dtos.RegistrationResponseDto;
import com.sit.qb.dtos.RestaurantRegisterDto;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.AuthServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @PostMapping("/register/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public StanderedSuccessResponse registerCustomer(@Valid @RequestBody CustomerRegisterDto dto) {
        RegistrationResponseDto response = authService.registerCustomer(dto);
        return new StanderedSuccessResponse(201, "Customer user created successfully", response);
    }

    @PostMapping("/register/delivery-agent")
    @ResponseStatus(HttpStatus.CREATED)
    public StanderedSuccessResponse registerAgent(@Valid @RequestBody AgentRegisterDto dto) {
        RegistrationResponseDto response = authService.registerAgent(dto);
        return new StanderedSuccessResponse(201, "Delivery agent user created successfully", response);
    }

    @PostMapping("/register/restaurant")
    @ResponseStatus(HttpStatus.CREATED)
    public StanderedSuccessResponse registerRestaurant(@Valid @RequestBody RestaurantRegisterDto dto) {
        RegistrationResponseDto response = authService.registerRestaurant(dto);
        return new StanderedSuccessResponse(201, "Restaurant user created successfully", response);
    }

    @PostMapping("/register/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public StanderedSuccessResponse registerAdmin(@Valid @RequestBody AdminRegisterDto dto) {
        RegistrationResponseDto response = authService.registerAdmin(dto);
        return new StanderedSuccessResponse(201, "Admin registered successfully", response);
    }

    @PostMapping("/login")
    public StanderedSuccessResponse login(@Valid @RequestBody LoginRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return new StanderedSuccessResponse(200, "Login successful", response);
    }
}
