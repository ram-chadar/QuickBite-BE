package com.sit.qb.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RestaurantProfileCreateDto {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 150, message = "Restaurant name must not exceed 150 characters")
    private String restaurantName;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 80, message = "Cuisine type must not exceed 80 characters")
    private String cuisineType;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
}
