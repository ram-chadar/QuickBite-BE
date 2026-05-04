package com.sit.qb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.repository.OrderRepository;

@Service
public class DeliveryServiceImpl {

	@Autowired
	private OrderRepository orderRepository;

	// QB-17: Full delivery report (orders with assigned agents)
	public List<DeliveryReportDto> getDeliveryReport() {
		return orderRepository.getDeliveryReport();
	}

}
