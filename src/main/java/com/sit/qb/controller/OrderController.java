package com.sit.qb.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.AssignAgentResponseDto;
import com.sit.qb.dtos.OrderDetailDto;
import com.sit.qb.dtos.OrderRequestDto;
import com.sit.qb.dtos.OrderStatusResponseDto;
import com.sit.qb.entity.Order;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.OrderServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderServiceImpl orderServiceImpl;

	// QB-6: Place order
	@PostMapping
	@PreAuthorize("hasRole('CUSTOMER')")
	public StanderedSuccessResponse placeOrder(@RequestBody @Valid OrderRequestDto order) {
		Order placed = orderServiceImpl.placeOrder(order);
		return new StanderedSuccessResponse(201, "Order Placed Successfully", placed);
	}

	// QB-7: Assign delivery agent
	@PutMapping("/{orderId}/assign-agent/{agentId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'DELIVERY_AGENT')")
	public StanderedSuccessResponse assignAgent(@PathVariable Long orderId, @PathVariable Long agentId) {
		AssignAgentResponseDto result = orderServiceImpl.assignAgent(orderId, agentId);
		return new StanderedSuccessResponse(200, "Agent assigned successfully", result);
	}

	// QB-8: Get full order details
	@GetMapping("/{orderId}")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'RESTAURANT', 'DELIVERY_AGENT', 'ADMIN')")
	public StanderedSuccessResponse getOrderDetail(@PathVariable Long orderId) {
		OrderDetailDto detail = orderServiceImpl.getOrderDetail(orderId);
		return new StanderedSuccessResponse(200, "Order loaded successfully", detail);
	}

	// QB-11: Filter orders by status (Criteria API)
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public StanderedSuccessResponse getOrdersByStatus(@RequestParam String status) {
		List<Order> orders = orderServiceImpl.getOrdersByStatus(status);
		return new StanderedSuccessResponse(200, "Orders loaded successfully", orders);
	}

	// QB-14: Get total bill for an order
	@GetMapping("/{orderId}/total")
	@PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
	public StanderedSuccessResponse getOrderTotal(@PathVariable Long orderId) {
		Double total = orderServiceImpl.getOrderTotal(orderId);
		return new StanderedSuccessResponse(200, "Total calculated successfully",
				Map.of("orderId", orderId, "totalBill", total));
	}

	// QB-16: Update order status
	@PatchMapping("/{orderId}/status")
	@PreAuthorize("hasAnyRole('DELIVERY_AGENT', 'ADMIN')")
	public StanderedSuccessResponse updateStatus(@PathVariable Long orderId, @RequestBody Map<String, String> body) {
		String newStatus = body.get("status");
		OrderStatusResponseDto result = orderServiceImpl.updateStatus(orderId, newStatus);
		return new StanderedSuccessResponse(200, "Order status updated successfully", result);
	}

}
