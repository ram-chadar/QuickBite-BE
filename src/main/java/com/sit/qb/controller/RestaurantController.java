package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.ProfileResponseDto;
import com.sit.qb.dtos.RestaurantProfileCreateDto;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.RestaurantProfile;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.RestaurantServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantServiceImpl service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('RESTAURANT')")
    public StanderedSuccessResponse createProfile(@Valid @RequestBody RestaurantProfileCreateDto dto) {
        ProfileResponseDto response = service.createProfile(dto);
        return new StanderedSuccessResponse(201, "Restaurant profile created successfully", response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public StanderedSuccessResponse getRestaurant(@PathVariable Long id) {
        RestaurantProfile restaurant = service.getRestaurant(id);
        return new StanderedSuccessResponse(200, "Restaurant Loaded Successfully", restaurant);
    }

    // QB-3: Add menu item
    @PostMapping("/{id}/menu")
    @PreAuthorize("hasRole('RESTAURANT')")
    public StanderedSuccessResponse addMenu(@RequestBody @Valid MenuItem menuItem, @PathVariable Long id) {
        MenuItem saved = service.addMenu(menuItem, id);
        return new StanderedSuccessResponse(201, "Menu Item Added Successfully", saved);
    }

    // QB-4: Get all restaurants
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public StanderedSuccessResponse getAllRestaurant() {
        List<RestaurantProfile> restaurants = service.getAllRestaurant();
        return new StanderedSuccessResponse(200, "Restaurants Loaded Successfully", restaurants);
    }

    // Get all orders for a specific restaurant
    @GetMapping("/{id}/orders")
    @PreAuthorize("hasAnyRole('RESTAURANT', 'ADMIN')")
    public StanderedSuccessResponse getRestaurantOrders(@PathVariable Long id) {
        List<Order> orders = service.getOrdersByRestaurant(id);
        return new StanderedSuccessResponse(200, "Restaurant orders loaded successfully", orders);
    }
}
