package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.CustomerProfileCreateDto;
import com.sit.qb.dtos.ProfileResponseDto;
import com.sit.qb.entity.CustomerProfile;
import com.sit.qb.entity.Order;
import com.sit.qb.projections.CustomerSummary;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.CustomerServiceImpl;
import com.sit.qb.service.OrderServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerServiceImpl service;

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public StanderedSuccessResponse createProfile(@Valid @RequestBody CustomerProfileCreateDto dto) {
        ProfileResponseDto response = service.createProfile(dto);
        return new StanderedSuccessResponse(201, "Customer profile created successfully", response);
    }

    // QB-13: Customer summary projection
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public StanderedSuccessResponse getCustomerSummary() {
        List<CustomerSummary> summary = service.getCustomerSummary();
        return new StanderedSuccessResponse(200, "Customer Summary Loaded Successfully", summary);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public StanderedSuccessResponse getCustomer(@PathVariable Long id) {
        CustomerProfile customer = service.getCustomer(id);
        if (customer != null) {
            return new StanderedSuccessResponse(200, "Customer Loaded Successfully", customer);
        } else {
            return new StanderedSuccessResponse(200, "Customer Not Found", null);
        }
    }

    @GetMapping("/byname/{name}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public StanderedSuccessResponse getCustomerByName(@PathVariable String name) {
        CustomerProfile customer = service.getCustomerByName(name);
        return new StanderedSuccessResponse(200, "Customer Loaded Successfully", customer);
    }

    @GetMapping("/{email}/{phone}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public StanderedSuccessResponse getCustomerByEmail_Phone(@PathVariable String email,
            @PathVariable String phone) {
        CustomerProfile customer = service.getCustomerByEmail_Phone(email, phone);
        return new StanderedSuccessResponse(200, "Customer Loaded Successfully", customer);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public StanderedSuccessResponse getAllCustomers() {
        return new StanderedSuccessResponse(200, "Customer Loaded Successfully", service.getAllCustomers());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StanderedSuccessResponse deleteCustomer(
            @PathVariable @Min(value = 1, message = "Invalid Id") Long id) {
        service.deleteCustomer(id);
        return new StanderedSuccessResponse(200, "Customer Deleted Successfully", true);
    }

    // QB-10: Get all orders of a customer
    @GetMapping("/{customerId}/orders")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public StanderedSuccessResponse getCustomerOrders(@PathVariable Long customerId) {
        List<Order> orders = orderServiceImpl.getOrdersByCustomer(customerId);
        return new StanderedSuccessResponse(200, "Orders Loaded Successfully", orders);
    }
}
