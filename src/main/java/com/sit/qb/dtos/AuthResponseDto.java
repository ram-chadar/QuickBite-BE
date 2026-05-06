package com.sit.qb.dtos;

public class AuthResponseDto {

	private Long id;
	private Long appUserId;
	private String name;
	private String email;
	private String role;
	private String phone;
	private String address;

	public AuthResponseDto(Long id, Long appUserId, String name, String email, String role, String phone, String address) {
		this.id = id;
		this.appUserId = appUserId;
		this.name = name;
		this.email = email;
		this.role = role;
		this.phone = phone;
		this.address = address;
	}

	public Long getId() { return id; }
	public Long getAppUserId() { return appUserId; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public String getRole() { return role; }
	public String getPhone() { return phone; }
	public String getAddress() { return address; }
}
