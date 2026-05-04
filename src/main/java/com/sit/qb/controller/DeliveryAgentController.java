package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.DeliveryAgentServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agents")
public class DeliveryAgentController {

	@Autowired
	private DeliveryAgentServiceImpl agentServiceImpl;

	// QB-5: Register delivery agent
	@PostMapping
	public StanderedSuccessResponse addDeliveryAgent(@RequestBody @Valid DeliveryAgent agent) {
		DeliveryAgent saved = agentServiceImpl.addDeliveryAgent(agent);
		return new StanderedSuccessResponse(201, "Delivery Agent Added Successfully", saved);
	}

}
