package com.sit.qb.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RestaurantRegisterDto {

	@NotBlank(message = "Email is required")
	@Email(message = "Invalid email")
	private String email;

	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;

	@NotBlank(message = "Restaurant name is required")
	@Size(max = 150, message = "Restaurant name must not exceed 150 characters")
	private String restaurantName;

	@Size(max = 100, message = "City must not exceed 100 characters")
	private String city;

	@Size(max = 80, message = "Cuisine type must not exceed 80 characters")
	private String cuisineType;

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String getRestaurantName() { return restaurantName; }
	public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }

	public String getCuisineType() { return cuisineType; }
	public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
}
