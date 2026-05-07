package com.sit.qb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sit.qb.entity.CustomerProfile;
import com.sit.qb.projections.CustomerSummary;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    Optional<CustomerProfile> findByName(String name);

    Optional<CustomerProfile> findByUser_EmailAndPhone(String email, String phone);

    Optional<CustomerProfile> findByUser_Id(Long userId);

    Optional<CustomerProfile> findByUser_Email(String email);

    List<CustomerSummary> findAllProjectedBy();
}
