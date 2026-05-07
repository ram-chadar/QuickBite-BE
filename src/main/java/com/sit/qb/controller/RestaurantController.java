package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.RestaurantProfile;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.RestaurantServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantServiceImpl service;

    @GetMapping("/{id}")
    public StanderedSuccessResponse getRestaurant(@PathVariable Long id) {
        RestaurantProfile restaurant = service.getRestaurant(id);
        return new StanderedSuccessResponse(200, "Restaurant Loaded Successfully", restaurant);
    }

    // QB-3: Add menu item
    @PostMapping("/{id}/menu")
    public StanderedSuccessResponse addMenu(@RequestBody @Valid MenuItem menuItem, @PathVariable Long id) {
        MenuItem saved = service.addMenu(menuItem, id);
        return new StanderedSuccessResponse(201, "Menu Item Added Successfully", saved);
    }

    // QB-4: Get all restaurants
    @GetMapping
    public StanderedSuccessResponse getAllRestaurant() {
        List<RestaurantProfile> restaurants = service.getAllRestaurant();
        return new StanderedSuccessResponse(200, "Restaurants Loaded Successfully", restaurants);
    }
}
