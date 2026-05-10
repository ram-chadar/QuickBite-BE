package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.ProfileResponseDto;
import com.sit.qb.dtos.RestaurantProfileCreateDto;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.RestaurantProfile;
import com.sit.qb.entity.User;
import com.sit.qb.entity.UserRole;
import com.sit.qb.exceptions.ConflictException;
import com.sit.qb.exceptions.ResourceNotFoundException;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.OrderRepository;
import com.sit.qb.repository.RestaurantProfileRepository;
import com.sit.qb.repository.UserRepository;

@Service
public class RestaurantServiceImpl {

    @Autowired
    private RestaurantProfileRepository restaurantProfileRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public ProfileResponseDto createProfile(RestaurantProfileCreateDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.getUserId()));

        if (user.getRole() != UserRole.RESTAURANT) {
            throw new ConflictException("User is not registered as a restaurant");
        }

        if (restaurantProfileRepository.findByUser_Id(user.getId()).isPresent()) {
            throw new ConflictException("Profile already exists for this user");
        }

        RestaurantProfile profile = new RestaurantProfile();
        profile.setName(dto.getRestaurantName());
        profile.setCity(dto.getCity());
        profile.setCuisineType(dto.getCuisineType());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setUser(user);
        profile = restaurantProfileRepository.save(profile);

        return new ProfileResponseDto(
                profile.getId(), user.getId(),
                profile.getName(), profile.getPhone(),
                profile.getAddress(), user.getRole().name());
    }

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

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        restaurantProfileRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantId));
        return orderRepository.findByRestaurantId(restaurantId);
    }
}
