package com.sit.qb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.AgentProfileCreateDto;
import com.sit.qb.dtos.ProfileResponseDto;
import com.sit.qb.entity.DeliveryAgentProfile;
import com.sit.qb.entity.User;
import com.sit.qb.entity.UserRole;
import com.sit.qb.exceptions.ConflictException;
import com.sit.qb.exceptions.ResourceNotFoundException;
import com.sit.qb.repository.DeliveryAgentProfileRepository;
import com.sit.qb.repository.UserRepository;

@Service
public class DeliveryAgentServiceImpl {

    @Autowired
    private DeliveryAgentProfileRepository agentRepository;

    @Autowired
    private UserRepository userRepository;

    public ProfileResponseDto createProfile(AgentProfileCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        if (user.getRole() != UserRole.DELIVERY_AGENT) {
            throw new ConflictException("User is not registered as a delivery agent");
        }

        if (agentRepository.findByUser_Id(user.getId()).isPresent()) {
            throw new ConflictException("Profile already exists for this user");
        }

        DeliveryAgentProfile profile = new DeliveryAgentProfile();
        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setUser(user);
        profile = agentRepository.save(profile);

        return new ProfileResponseDto(
                profile.getId(), user.getId(),
                profile.getName(), profile.getPhone(),
                null, user.getRole().name());
    }
}
