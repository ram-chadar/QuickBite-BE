package com.sit.qb.dtos;

public class ProfileResponseDto {

    private Long profileId;
    private Long userId;
    private String name;
    private String phone;
    private String address;
    private String role;

    public ProfileResponseDto(Long profileId, Long userId, String name, String phone, String address, String role) {
        this.profileId = profileId;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    public Long getProfileId() { return profileId; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
}
