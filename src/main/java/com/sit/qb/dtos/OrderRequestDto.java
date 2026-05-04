package com.sit.qb.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {

	@NotNull(message = "customerId is required")
	private Long customerId;

	@NotNull(message = "items list is required")
	@NotEmpty(message = "Order must contain at least one item")
	private List<Menu_Qty> items;

	public OrderRequestDto() {
	}

	public OrderRequestDto(Long customerId, List<Menu_Qty> items) {
		this.customerId = customerId;
		this.items = items;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public List<Menu_Qty> getItems() {
		return items;
	}

	public void setItems(List<Menu_Qty> items) {
		this.items = items;
	}

}
