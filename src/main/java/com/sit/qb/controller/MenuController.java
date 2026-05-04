package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.TopItemDto;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.exceptions.IllegalStateTransitionException;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.MenuItemServiceImpl;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

	@Autowired
	private MenuItemServiceImpl menuItemService;

	// QB-9: Search menu items by keyword
	@GetMapping("/search")
	public StanderedSuccessResponse searchByKeyword(@RequestParam String keyword) {
		if (keyword == null || keyword.isBlank()) {
			throw new IllegalStateTransitionException("keyword must not be blank");
		}
		List<MenuItem> items = menuItemService.searchByKeyword(keyword);
		return new StanderedSuccessResponse(200, "Menu items found", items);
	}

	// QB-12: Get available items at or below maxPrice
	@GetMapping
	public StanderedSuccessResponse getMenuByMaxPrice(@RequestParam Double maxPrice) {
		List<MenuItem> items = menuItemService.getMenuItemsBelowPrice(maxPrice);
		return new StanderedSuccessResponse(200, "Menu items loaded successfully", items);
	}

	// QB-15: Top 3 most ordered items
	@GetMapping("/top3")
	public StanderedSuccessResponse getTop3() {
		List<TopItemDto> top3 = menuItemService.getTop3OrderedItems();
		return new StanderedSuccessResponse(200, "Top 3 menu items loaded", top3);
	}

}
