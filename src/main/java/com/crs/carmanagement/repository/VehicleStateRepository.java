package com.crs.carmanagement.repository;

import com.crs.carmanagement.entity.VehicleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for VehicleState entity
 */
@Repository
public interface VehicleStateRepository extends JpaRepository<VehicleState, Long> {

    /**
     * Find vehicle state by vehicle ID
     */
    Optional<VehicleState> findByVehicleId(Long vehicleId);

    /**
     * Delete vehicle state by vehicle ID
     */
    void deleteByVehicleId(Long vehicleId);
}
