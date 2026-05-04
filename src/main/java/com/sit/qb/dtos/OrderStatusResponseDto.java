package com.sit.qb.dtos;

import java.time.LocalDateTime;

import com.sit.qb.enums.OrderStatus;

public class OrderStatusResponseDto {

	private Long orderId;
	private OrderStatus previousStatus;
	private OrderStatus newStatus;
	private LocalDateTime updatedAt;

	public OrderStatusResponseDto(Long orderId, OrderStatus previousStatus, OrderStatus newStatus,
			LocalDateTime updatedAt) {
		this.orderId = orderId;
		this.previousStatus = previousStatus;
		this.newStatus = newStatus;
		this.updatedAt = updatedAt;
	}

	public Long getOrderId() {
		return orderId;
	}

	public OrderStatus getPreviousStatus() {
		return previousStatus;
	}

	public OrderStatus getNewStatus() {
		return newStatus;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

}
