package com.sit.qb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.dtos.DeliveryReportDto;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.DeliveryServiceImpl;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

	@Autowired
	private DeliveryServiceImpl deliveryService;

	// QB-17: Generate full delivery report
	@GetMapping("/report")
	public StanderedSuccessResponse getDeliveryReport() {
		List<DeliveryReportDto> report = deliveryService.getDeliveryReport();
		return new StanderedSuccessResponse(200, "Delivery report generated", report);
	}

}
