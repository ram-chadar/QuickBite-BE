package com.sit.qb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.TopItemDto;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.exceptions.IllegalStateTransitionException;
import com.sit.qb.repository.MenuItemRepository;

@Service
public class MenuItemServiceImpl {

	@Autowired
	private MenuItemRepository menuItemRepository;

	// QB-9: Search by keyword (case-insensitive LIKE)
	public List<MenuItem> searchByKeyword(String keyword) {
		return menuItemRepository.searchByKeyword(keyword);
	}

	// QB-12: Get available items at or below maxPrice (Specification / Criteria API)
	public List<MenuItem> getMenuItemsBelowPrice(Double maxPrice) {
		if (maxPrice == null || maxPrice <= 0) {
			throw new IllegalStateTransitionException("maxPrice must be a positive value");
		}
		Specification<MenuItem> spec = (root, query, cb) ->
			cb.and(
				cb.lessThanOrEqualTo(root.get("price"), maxPrice),
				cb.isTrue(root.get("isAvailable"))
			);
		return menuItemRepository.findAll(spec);
	}

	// QB-15: Top 3 most ordered items
	public List<TopItemDto> getTop3OrderedItems() {
		return menuItemRepository.findTop3OrderedItems(PageRequest.of(0, 3));
	}

}
