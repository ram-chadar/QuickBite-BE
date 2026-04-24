package com.sit.qb.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.entity.Order;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@PostMapping
	public Order placeOrder(@RequestBody OrderRequestDto order	) {
		
		return null;

	}

}
