package com.crs.carmanagement.repository;

import com.crs.carmanagement.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for VehicleModel entity
 */
@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {

    /**
     * Find vehicle model by modelId
     */
    Optional<VehicleModel> findByModelId(String modelId);

    /**
     * Check if model exists by modelId
     */
    boolean existsByModelId(String modelId);
}
