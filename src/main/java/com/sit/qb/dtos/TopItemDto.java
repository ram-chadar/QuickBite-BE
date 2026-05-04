package com.sit.qb.dtos;

public class TopItemDto {

	private String itemName;
	private Long orderCount;

	public TopItemDto(String itemName, Long orderCount) {
		this.itemName = itemName;
		this.orderCount = orderCount;
	}

	public String getItemName() {
		return itemName;
	}

	public Long getOrderCount() {
		return orderCount;
	}

}
