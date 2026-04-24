package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Restaurant;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.RestaurantRepository;

@Service
public class RestaurantServiceImpl {

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menItemRepository;

	public Restaurant register(Restaurant restaurant) {
		return restaurantRepository.save(restaurant);

	}
	
	public Restaurant getRestaurant(Long id) {
		Optional<Restaurant> restaurant = restaurantRepository.findById(id);
		if(restaurant.isPresent()) {
			return restaurant.get();
		}
		return null;
	}

	public MenuItem addMenu(MenuItem menuItem, Long id) {
		Optional<Restaurant> restaurant = restaurantRepository.findById(id);
		if (restaurant.isPresent()) {
			menuItem.setRestaurant(restaurant.get());
			System.out.println(menuItem);
			return menItemRepository.save(menuItem);
		}
		return null;
	}

	public List<Restaurant> getAllRestaurant() {	
		return restaurantRepository.findAll();
	}

	

	

}
