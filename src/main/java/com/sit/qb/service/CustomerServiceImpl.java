package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.CustomerProfile;
import com.sit.qb.projections.CustomerSummary;
import com.sit.qb.repository.CustomerProfileRepository;

@Service
public class CustomerServiceImpl {

    @Autowired
    private CustomerProfileRepository repository;

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
