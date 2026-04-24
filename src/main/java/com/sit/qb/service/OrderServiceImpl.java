package com.sit.qb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.MenuItem;
import com.sit.qb.entity.Order;
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
		if(customer!=null) {
			
			// get all menu ids
			 List<Long> menuIds = orderDto.getItems().stream().map(menuQty -> menuQty.getMenuItemId()).collect(Collectors.toList());
			 
			 List<Boolean> isMenuExist=new ArrayList<>();
			 for (Long id : menuIds) {
				MenuItem menuItem = menuItemRepository.findById(id).get();
				
				if(menuItem!=null) {
					if(menuItem.getIsAvailable()==true) {
						isMenuExist.add(true);
					}else {
						isMenuExist.add(false);
					}
					
				}else {
					isMenuExist.add(false);
				}
			}
			 
			 if(isMenuExist.contains(true)) {
				 // place your order
				 
				 Order order=new Order();
				 order.setCustomer(customer);
				 
				 LocalDateTime dateTime = LocalDateTime.now();
				 order.setOrderDate(dateTime);
				 
				 
				 // set all order Item
			
				 
					orderRepository.save(order);
			 }
			 
			 
		}
		
		
	

		return null;

	}

}
