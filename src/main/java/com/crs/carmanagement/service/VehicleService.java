package com.crs.carmanagement.service;

import com.crs.carmanagement.dto.request.CreateVehicleRequest;
import com.crs.carmanagement.dto.request.UpdateVehicleRequest;
import com.crs.carmanagement.dto.response.VehicleDetailResponse;
import com.crs.carmanagement.dto.response.VehicleResponse;
import com.crs.carmanagement.enums.VehicleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Vehicle operations
 */
public interface VehicleService {

    /**
     * Create a new vehicle
     */
    VehicleDetailResponse createVehicle(CreateVehicleRequest request);

    /**
     * Get vehicle by ID with full details
     */
    VehicleDetailResponse getVehicleById(Long id);

    /**
     * Get all vehicles with pagination
     */
    Page<VehicleResponse> getAllVehicles(Pageable pageable);

    /**
     * Update vehicle information
     */
    VehicleDetailResponse updateVehicle(Long id, UpdateVehicleRequest request);

    /**
     * Delete vehicle
     */
    void deleteVehicle(Long id);

    /**
     * Get vehicles by status
     */
    List<VehicleResponse> getVehiclesByStatus(VehicleStatus status);

    /**
     * Get vehicles by fleet hub
     */
    List<VehicleResponse> getVehiclesByFleetHub(Long hubId);
}
