package com.sit.qb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sit.qb.dtos.AgentRegisterDto;
import com.sit.qb.dtos.AuthResponseDto;
import com.sit.qb.dtos.CustomerRegisterDto;
import com.sit.qb.dtos.LoginRequestDto;
import com.sit.qb.dtos.RestaurantRegisterDto;
import com.sit.qb.entity.AppUser;
import com.sit.qb.entity.Customer;
import com.sit.qb.entity.DeliveryAgent;
import com.sit.qb.entity.Restaurant;
import com.sit.qb.entity.UserRole;
import com.sit.qb.exceptions.DuplicateEmailException;
import com.sit.qb.exceptions.InvalidCredentialsException;
import com.sit.qb.repository.AppUserRepository;
import com.sit.qb.repository.CustomerRepository;
import com.sit.qb.repository.DeliveryAgentRepository;
import com.sit.qb.repository.RestaurantRepository;

@Service
public class AuthServiceImpl {

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private DeliveryAgentRepository agentRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Transactional
	public AuthResponseDto registerCustomer(CustomerRegisterDto dto) {
		if (appUserRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateEmailException("Email already registered");
		}

		AppUser appUser = new AppUser();
		appUser.setEmail(dto.getEmail());
		appUser.setPassword(dto.getPassword());
		appUser.setRole(UserRole.CUSTOMER);
		appUser = appUserRepository.save(appUser);

		Customer customer = new Customer();
		customer.setName(dto.getName());
		customer.setEmail(dto.getEmail());
		customer.setPhone(dto.getPhone());
		customer.setAddress(dto.getAddress());
		customer.setPassword(dto.getPassword());
		customer.setAppUser(appUser);
		customer = customerRepository.save(customer);

		return new AuthResponseDto(
				customer.getId(), appUser.getId(),
				customer.getName(), appUser.getEmail(),
				appUser.getRole().name(),
				customer.getPhone(), customer.getAddress());
	}

	@Transactional
	public AuthResponseDto registerAgent(AgentRegisterDto dto) {
		if (appUserRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateEmailException("Email already registered");
		}

		AppUser appUser = new AppUser();
		appUser.setEmail(dto.getEmail());
		appUser.setPassword(dto.getPassword());
		appUser.setRole(UserRole.DELIVERY_AGENT);
		appUser = appUserRepository.save(appUser);

		DeliveryAgent agent = new DeliveryAgent();
		agent.setName(dto.getName());
		agent.setPhone(dto.getPhone());
		agent.setAppUser(appUser);
		agent = agentRepository.save(agent);

		return new AuthResponseDto(
				agent.getId(), appUser.getId(),
				agent.getName(), appUser.getEmail(),
				appUser.getRole().name(),
				agent.getPhone(), null);
	}

	@Transactional
	public AuthResponseDto registerRestaurant(RestaurantRegisterDto dto) {
		if (appUserRepository.existsByEmail(dto.getEmail())) {
			throw new DuplicateEmailException("Email already registered");
		}

		AppUser appUser = new AppUser();
		appUser.setEmail(dto.getEmail());
		appUser.setPassword(dto.getPassword());
		appUser.setRole(UserRole.RESTAURANT_OWNER);
		appUser = appUserRepository.save(appUser);

		Restaurant restaurant = new Restaurant();
		restaurant.setName(dto.getRestaurantName());
		restaurant.setCity(dto.getCity());
		restaurant.setCuisineType(dto.getCuisineType());
		restaurant.setAppUser(appUser);
		restaurant = restaurantRepository.save(restaurant);

		return new AuthResponseDto(
				restaurant.getId(), appUser.getId(),
				restaurant.getName(), appUser.getEmail(),
				appUser.getRole().name(),
				null, null);
	}

	public AuthResponseDto login(LoginRequestDto dto) {
		AppUser appUser = appUserRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!dto.getPassword().equals(appUser.getPassword())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		return buildLoginResponse(appUser);
	}

	private AuthResponseDto buildLoginResponse(AppUser appUser) {
		switch (appUser.getRole()) {
			case CUSTOMER: {
				Customer customer = customerRepository.findByAppUser_Id(appUser.getId())
						.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
				return new AuthResponseDto(
						customer.getId(), appUser.getId(),
						customer.getName(), appUser.getEmail(),
						appUser.getRole().name(),
						customer.getPhone(), customer.getAddress());
			}
			case DELIVERY_AGENT: {
				DeliveryAgent agent = agentRepository.findByAppUser_Id(appUser.getId())
						.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
				return new AuthResponseDto(
						agent.getId(), appUser.getId(),
						agent.getName(), appUser.getEmail(),
						appUser.getRole().name(),
						agent.getPhone(), null);
			}
			case RESTAURANT_OWNER: {
				Restaurant restaurant = restaurantRepository.findByAppUser_Id(appUser.getId())
						.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
				return new AuthResponseDto(
						restaurant.getId(), appUser.getId(),
						restaurant.getName(), appUser.getEmail(),
						appUser.getRole().name(),
						null, null);
			}
			default:
				throw new InvalidCredentialsException("Invalid email or password");
		}
	}
}
