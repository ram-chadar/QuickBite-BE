package com.sit.qb.dtos;

import java.util.List;

public class OrderRequestDto {
	
	private long customerId;
	private List<Menu_Qty> items;
	
	public OrderRequestDto() {
		// TODO Auto-generated constructor stub
	}

	public OrderRequestDto(long customerId, List<Menu_Qty> items) {
		super();
		this.customerId = customerId;
		this.items = items;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public List<Menu_Qty> getItems() {
		return items;
	}

	public void setItems(List<Menu_Qty> items) {
		this.items = items;
	}
	
	
	

}
