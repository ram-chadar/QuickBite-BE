package com.sit.qb.dtos;

public class OrderItemDto {

	private String item;
	private int qty;
	private double price;

	public OrderItemDto(String item, int qty, double price) {
		this.item = item;
		this.qty = qty;
		this.price = price;
	}

	public String getItem() {
		return item;
	}

	public int getQty() {
		return qty;
	}

	public double getPrice() {
		return price;
	}

}
