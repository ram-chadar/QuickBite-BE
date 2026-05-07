package com.sit.qb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sit.qb.entity.DeliveryAgentProfile;

@Repository
public interface DeliveryAgentProfileRepository extends JpaRepository<DeliveryAgentProfile, Long> {

    Optional<DeliveryAgentProfile> findByUser_Id(Long userId);
}
