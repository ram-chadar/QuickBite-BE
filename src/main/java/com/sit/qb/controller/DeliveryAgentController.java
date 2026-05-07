package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.AgentProfileCreateDto;
import com.sit.qb.dtos.ProfileResponseDto;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.DeliveryAgentServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agents")
public class DeliveryAgentController {

    @Autowired
    private DeliveryAgentServiceImpl agentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('DELIVERY_AGENT')")
    public StanderedSuccessResponse createProfile(@Valid @RequestBody AgentProfileCreateDto dto) {
        ProfileResponseDto response = agentService.createProfile(dto);
        return new StanderedSuccessResponse(201, "Delivery agent profile created successfully", response);
    }
}
