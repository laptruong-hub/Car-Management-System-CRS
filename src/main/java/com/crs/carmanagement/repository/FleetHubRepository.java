package com.crs.carmanagement.repository;

import com.crs.carmanagement.entity.FleetHub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for FleetHub entity
 */
@Repository
public interface FleetHubRepository extends JpaRepository<FleetHub, Long> {
}
