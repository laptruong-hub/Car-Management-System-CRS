package com.crs.carmanagement.repository;

import com.crs.carmanagement.entity.Vehicle;
import com.crs.carmanagement.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Vehicle entity with custom queries
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Find vehicle by plate number
     */
    Optional<Vehicle> findByPlateNumber(String plateNumber);

    /**
     * Find vehicles by status
     */
    List<Vehicle> findByStatus(VehicleStatus status);

    /**
     * Find vehicles by status with pagination
     */
    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);

    /**
     * Find vehicles by fleet hub
     */
    List<Vehicle> findByFleetHubId(Long hubId);

    /**
     * Find virtual or physical vehicles
     */
    List<Vehicle> findByIsVirtual(Boolean isVirtual);

    /**
     * Check if plate number exists
     */
    boolean existsByPlateNumber(String plateNumber);

    /**
     * Check if VIN exists
     */
    boolean existsByVin(String vin);

    /**
     * Find vehicles with their models (fetch join to avoid N+1)
     */
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.model WHERE v.id = :id")
    Optional<Vehicle> findByIdWithModel(@Param("id") Long id);

    /**
     * Find all vehicles with models (optimized for list views)
     */
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.model")
    List<Vehicle> findAllWithModels();
}
