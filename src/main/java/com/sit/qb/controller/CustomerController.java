package com.sit.qb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sit.qb.entity.Customer;
import com.sit.qb.response.StanderedSuccessResponse;
import com.sit.qb.service.CustomerServiceImpl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
	private CustomerServiceImpl service;

	@PostMapping
	public Customer saveUpdate(@RequestBody @Valid Customer customer) {
		return service.register(customer);
	}

	@GetMapping("/{id}")
	public StanderedSuccessResponse getCustomer(@PathVariable Long id) {
		StanderedSuccessResponse response=null;
		Customer customer = service.getCustomer(id);
		if(customer!=null) {
			response = new StanderedSuccessResponse(200, "Customer Loaded Successfully", customer);
		}else {
			response = new StanderedSuccessResponse(200, "Customer Not Found", customer);
		}
		
		return response;
	}

	@GetMapping("/byname/{name}")
	public StanderedSuccessResponse getCustomerByName(
			@PathVariable @Pattern(regexp = "^[A-Za-z]{2,}(?:\\s+[A-Za-z]{2,})?$", message = "Invalid name") String name) {
		
		Customer customer = service.getCustomerByName(name);
		StanderedSuccessResponse response = new StanderedSuccessResponse(200, "Customer Loaded Successfully", customer);
		return response;
	}

	@GetMapping("/{email}/{phone}")
	public Customer getCustomerByEmail_Phone(@PathVariable String email, @PathVariable String phone) {
		return service.getCustomerByEmail_Phone(email, phone);
	}

	@GetMapping
	public StanderedSuccessResponse getAllCustomers() {

		StanderedSuccessResponse response = new StanderedSuccessResponse(200, "Customer Loaded Successfully",
				service.getAllCustomers());
		return response;
	}

	@DeleteMapping("/{id}")
	public StanderedSuccessResponse deleteCustomer(@PathVariable @Min(value = 1, message = "Invalid Id") Long id) {
		service.deleteCustomer(id);

		StanderedSuccessResponse response = new StanderedSuccessResponse(200, "Customer Deleted Successfully", true);
		return response;
	}

}
