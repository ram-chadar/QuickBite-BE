package com.sit.qb.dtos;

public class AssignAgentResponseDto {

	private String message;
	private Long orderId;
	private Long agentId;
	private String agentName;

	public AssignAgentResponseDto(String message, Long orderId, Long agentId, String agentName) {
		this.message = message;
		this.orderId = orderId;
		this.agentId = agentId;
		this.agentName = agentName;
	}

	public String getMessage() {
		return message;
	}

	public Long getOrderId() {
		return orderId;
	}

	public Long getAgentId() {
		return agentId;
	}

	public String getAgentName() {
		return agentName;
	}

}
