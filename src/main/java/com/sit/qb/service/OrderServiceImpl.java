package com.sit.qb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.Menu_Qty;
import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
import com.sit.qb.entity.OrderItem;
import com.sit.qb.repository.MenuItemRepository;
import com.sit.qb.repository.OrderRepository;

@Service
public class OrderServiceImpl {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerServiceImpl customerServiceImpl;

	@Autowired
	private MenuItemRepository menuItemRepository;

	public Order placeOrder(OrderRequestDto orderDto) {

		Customer customer = customerServiceImpl.getCustomer(orderDto.getCustomerId());
		if (customer == null) {
			throw new RuntimeException("Customer not found");
		}

		Order order = new Order();
		order.setCustomer(customer);
		order.setOrderDate(LocalDateTime.now());

		List<OrderItem> orderItems = new ArrayList<>();
		double totalAmount = 0.0;

		// Loop through each item from request
		for (Menu_Qty itemDto : orderDto.getItems()) {

			MenuItem menuItem = menuItemRepository.findById(itemDto.getMenuItemId())
					.orElseThrow(() -> new RuntimeException("Menu item not found: " + itemDto.getMenuItemId()));

			if (!menuItem.getIsAvailable()) {
				throw new RuntimeException("Menu item not available: " + menuItem.getName());
			}

			OrderItem orderItem = new OrderItem();
			orderItem.setMenuItem(menuItem);
			orderItem.setQuantity((int)itemDto.getQuantity());
			orderItem.setUnitPrice(menuItem.getPrice());

			// IMPORTANT: set relation
			orderItem.setOrder(order);

			orderItems.add(orderItem);

			// Calculate total
			totalAmount += menuItem.getPrice() * itemDto.getQuantity();
		}

		// Set values in order
		order.setOrderItems(orderItems);
		order.setTotalAmount(totalAmount);

		// Save (Cascade will save orderItems automatically)
		return orderRepository.save(order);
	}

}
