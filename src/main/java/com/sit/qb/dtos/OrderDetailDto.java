package com.sit.qb.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.sit.qb.enums.OrderStatus;

public class OrderDetailDto {

	private Long orderId;
	private String customerName;
	private OrderStatus status;
	private LocalDateTime orderDate;
	private List<OrderItemDto> items;
	private Double total;

	public OrderDetailDto(Long orderId, String customerName, OrderStatus status, LocalDateTime orderDate,
			List<OrderItemDto> items, Double total) {
		this.orderId = orderId;
		this.customerName = customerName;
		this.status = status;
		this.orderDate = orderDate;
		this.items = items;
		this.total = total;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public List<OrderItemDto> getItems() {
		return items;
	}

	public Double getTotal() {
		return total;
	}

}
