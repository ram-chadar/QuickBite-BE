package com.sit.qb.dtos;

public class AuthResponseDto {

    private String token;
    private Long profileId;
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String address;

    public AuthResponseDto(String token, Long profileId, Long userId, String name, String email,
                           String role, String phone, String address) {
        this.token = token;
        this.profileId = profileId;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.address = address;
    }

    public String getToken() { return token; }
    public Long getProfileId() { return profileId; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
}
