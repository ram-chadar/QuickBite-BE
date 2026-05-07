package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.RestaurantProfile;
import com.sit.qb.exceptions.ResourceNotFoundException;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.RestaurantProfileRepository;

@Service
public class RestaurantServiceImpl {

    @Autowired
    private RestaurantProfileRepository restaurantProfileRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    public RestaurantProfile getRestaurant(Long id) {
        Optional<RestaurantProfile> restaurant = restaurantProfileRepository.findById(id);
        return restaurant.orElse(null);
    }

    public MenuItem addMenu(MenuItem menuItem, Long id) {
        RestaurantProfile restaurant = restaurantProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + id));
        menuItem.setRestaurant(restaurant);
        return menuItemRepository.save(menuItem);
    }

    public List<RestaurantProfile> getAllRestaurant() {
        return restaurantProfileRepository.findAll();
    }
}
