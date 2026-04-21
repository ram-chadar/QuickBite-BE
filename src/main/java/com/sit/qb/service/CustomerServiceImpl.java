package com.sit.qb.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sit.qb.entity.Customer;
import com.sit.qb.repository.CustomerRepository;

@Service
public class CustomerServiceImpl {

	@Autowired
	private CustomerRepository repository;

	public Customer register(Customer customer) {
		return repository.save(customer);
	}

	public Customer getCustomer(Long id) {

		Optional<Customer> customer = repository.findById(id);
		if (customer.isPresent()) {
			return customer.get();
		}
		return null;
	}

	public List<Customer> getAllCustomers() {
		return repository.findAll();

	}

	public void deleteCustomer(Long id) {
		repository.deleteById(id);
		
	}

}
