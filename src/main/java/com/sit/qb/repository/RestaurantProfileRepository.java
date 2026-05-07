package com.sit.qb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sit.qb.entity.RestaurantProfile;

@Repository
public interface RestaurantProfileRepository extends JpaRepository<RestaurantProfile, Long> {

    Optional<RestaurantProfile> findByUser_Id(Long userId);
}
