package com.sit.qb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sit.qb.dtos.AdminRegisterDto;
import com.sit.qb.dtos.AgentRegisterDto;
import com.sit.qb.dtos.AuthResponseDto;
import com.sit.qb.dtos.CustomerRegisterDto;
import com.sit.qb.dtos.LoginRequestDto;
import com.sit.qb.dtos.RestaurantRegisterDto;
import com.sit.qb.entity.CustomerProfile;
import com.sit.qb.entity.DeliveryAgentProfile;
import com.sit.qb.entity.RestaurantProfile;
import com.sit.qb.entity.User;
import com.sit.qb.entity.UserRole;
import com.sit.qb.exceptions.DuplicateEmailException;
import com.sit.qb.exceptions.InvalidCredentialsException;
import com.sit.qb.repository.CustomerProfileRepository;
import com.sit.qb.repository.DeliveryAgentProfileRepository;
import com.sit.qb.repository.RestaurantProfileRepository;
import com.sit.qb.repository.UserRepository;

@Service
public class AuthServiceImpl {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private DeliveryAgentProfileRepository agentProfileRepository;

    @Autowired
    private RestaurantProfileRepository restaurantProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponseDto registerCustomer(CustomerRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);
        user = userRepository.save(user);

        CustomerProfile profile = new CustomerProfile();
        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setUser(user);
        profile = customerProfileRepository.save(profile);

        return new AuthResponseDto(
                profile.getId(), user.getId(),
                profile.getName(), user.getEmail(),
                user.getRole().name(),
                profile.getPhone(), profile.getAddress());
    }

    @Transactional
    public AuthResponseDto registerAgent(AgentRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.DELIVERY_AGENT);
        user.setEnabled(true);
        user = userRepository.save(user);

        DeliveryAgentProfile profile = new DeliveryAgentProfile();
        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setUser(user);
        profile = agentProfileRepository.save(profile);

        return new AuthResponseDto(
                profile.getId(), user.getId(),
                profile.getName(), user.getEmail(),
                user.getRole().name(),
                profile.getPhone(), null);
    }

    @Transactional
    public AuthResponseDto registerRestaurant(RestaurantRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.RESTAURANT);
        user.setEnabled(true);
        user = userRepository.save(user);

        RestaurantProfile profile = new RestaurantProfile();
        profile.setName(dto.getRestaurantName());
        profile.setCity(dto.getCity());
        profile.setCuisineType(dto.getCuisineType());
        profile.setUser(user);
        profile = restaurantProfileRepository.save(profile);

        return new AuthResponseDto(
                profile.getId(), user.getId(),
                profile.getName(), user.getEmail(),
                user.getRole().name(),
                null, null);
    }

    @Transactional
    public AuthResponseDto registerAdmin(AdminRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already registered");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRole.ADMIN);
        user.setEnabled(true);
        user = userRepository.save(user);

        return new AuthResponseDto(
                null, user.getId(),
                null, user.getEmail(),
                user.getRole().name(),
                null, null);
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return buildLoginResponse(user);
    }

    private AuthResponseDto buildLoginResponse(User user) {
        switch (user.getRole()) {
            case CUSTOMER: {
                CustomerProfile profile = customerProfileRepository.findByUser_Id(user.getId())
                        .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
                return new AuthResponseDto(
                        profile.getId(), user.getId(),
                        profile.getName(), user.getEmail(),
                        user.getRole().name(),
                        profile.getPhone(), profile.getAddress());
            }
            case DELIVERY_AGENT: {
                DeliveryAgentProfile profile = agentProfileRepository.findByUser_Id(user.getId())
                        .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
                return new AuthResponseDto(
                        profile.getId(), user.getId(),
                        profile.getName(), user.getEmail(),
                        user.getRole().name(),
                        profile.getPhone(), null);
            }
            case RESTAURANT: {
                RestaurantProfile profile = restaurantProfileRepository.findByUser_Id(user.getId())
                        .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
                return new AuthResponseDto(
                        profile.getId(), user.getId(),
                        profile.getName(), user.getEmail(),
                        user.getRole().name(),
                        null, null);
            }
            case ADMIN: {
                return new AuthResponseDto(
                        null, user.getId(),
                        null, user.getEmail(),
                        user.getRole().name(),
                        null, null);
            }
            default:
                throw new InvalidCredentialsException("Invalid email or password");
        }
    }
}
