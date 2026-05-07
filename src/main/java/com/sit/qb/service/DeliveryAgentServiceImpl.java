package com.sit.qb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.DeliveryAgentProfile;
import com.sit.qb.repository.DeliveryAgentProfileRepository;

@Service
public class DeliveryAgentServiceImpl {

    @Autowired
    private DeliveryAgentProfileRepository agentRepository;

    public DeliveryAgentProfile addDeliveryAgent(DeliveryAgentProfile agent) {
        return agentRepository.save(agent);
    }
}
