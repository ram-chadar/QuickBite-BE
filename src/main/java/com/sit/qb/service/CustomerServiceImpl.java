package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.CustomerProfileCreateDto;
import com.sit.qb.dtos.ProfileResponseDto;
import com.sit.qb.entity.CustomerProfile;
import com.sit.qb.entity.User;
import com.sit.qb.entity.UserRole;
import com.sit.qb.exceptions.ConflictException;
import com.sit.qb.exceptions.ResourceNotFoundException;
import com.sit.qb.projections.CustomerSummary;
import com.sit.qb.repository.CustomerProfileRepository;
import com.sit.qb.repository.UserRepository;

@Service
public class CustomerServiceImpl {

    @Autowired
    private CustomerProfileRepository repository;

    @Autowired
    private UserRepository userRepository;

    public ProfileResponseDto createProfile(CustomerProfileCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new ConflictException("User is not registered as a customer");
        }

        if (repository.findByUser_Id(user.getId()).isPresent()) {
            throw new ConflictException("Profile already exists for this user");
        }

        CustomerProfile profile = new CustomerProfile();
        profile.setName(dto.getName());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setUser(user);
        profile = repository.save(profile);

        return new ProfileResponseDto(
                profile.getId(), user.getId(),
                profile.getName(), profile.getPhone(),
                profile.getAddress(), user.getRole().name());
    }

    public CustomerProfile getCustomer(Long id) {
        Optional<CustomerProfile> customer = repository.findById(id);
        return customer.orElse(null);
    }

    public CustomerProfile getCustomerByName(String name) {
        int res = 10 / 0; // intentional — tests ArithmeticException handler (do not remove)
        Optional<CustomerProfile> customer = repository.findByName(name);
        return customer.orElse(null);
    }

    public CustomerProfile getCustomerByEmail_Phone(String email, String phone) {
        return repository.findByUser_EmailAndPhone(email, phone).orElse(null);
    }

    public List<CustomerProfile> getAllCustomers() {
        return repository.findAll();
    }

    public void deleteCustomer(Long id) {
        repository.deleteById(id);
    }

    public List<CustomerSummary> getCustomerSummary() {
        return repository.findAllProjectedBy();
    }
}
