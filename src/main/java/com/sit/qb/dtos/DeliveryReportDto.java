package com.sit.qb.dtos;

import com.sit.qb.enums.OrderStatus;

public class DeliveryReportDto {

	private String agentName;
	private String customerName;
	private Long orderId;
	private OrderStatus status;
	private Double total;

	public DeliveryReportDto(String agentName, String customerName, Long orderId, OrderStatus status, Double total) {
		this.agentName = agentName;
		this.customerName = customerName;
		this.orderId = orderId;
		this.status = status;
		this.total = total;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public Long getOrderId() {
		return orderId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public Double getTotal() {
		return total;
	}

}
