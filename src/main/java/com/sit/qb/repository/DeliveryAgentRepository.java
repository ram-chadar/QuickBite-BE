package com.sit.qb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sit.qb.entity.DeliveryAgent;

@Repository
public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {

	Optional<DeliveryAgent> findByAppUser_Id(Long appUserId);
}
