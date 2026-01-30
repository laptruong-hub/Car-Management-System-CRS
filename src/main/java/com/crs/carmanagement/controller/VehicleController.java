package com.crs.carmanagement.controller;

import com.crs.carmanagement.dto.request.CreateVehicleRequest;
import com.crs.carmanagement.dto.request.UpdateVehicleRequest;
import com.crs.carmanagement.dto.response.ApiResponse;
import com.crs.carmanagement.dto.response.VehicleDetailResponse;
import com.crs.carmanagement.dto.response.VehicleResponse;
import com.crs.carmanagement.enums.VehicleStatus;
import com.crs.carmanagement.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Vehicle operations
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicle", description = "Vehicle management APIs")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Create a new vehicle
     */
    @PostMapping
    @Operation(summary = "Create a new vehicle", description = "Create a new vehicle with initial state")
    public ResponseEntity<ApiResponse<VehicleDetailResponse>> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request) {

        log.info("REST request to create vehicle: {}", request.getPlateNumber());

        VehicleDetailResponse response = vehicleService.createVehicle(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Vehicle created successfully"));
    }

    /**
     * Get vehicle by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Get detailed vehicle information including state and recent events")
    public ResponseEntity<ApiResponse<VehicleDetailResponse>> getVehicleById(@PathVariable Long id) {

        log.info("REST request to get vehicle: {}", id);

        VehicleDetailResponse response = vehicleService.getVehicleById(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Vehicle retrieved successfully"));
    }

    /**
     * Get all vehicles with pagination
     */
    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Get all vehicles with pagination and sorting")
    public ResponseEntity<ApiResponse<Page<VehicleResponse>>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.info("REST request to get all vehicles - page: {}, size: {}", page, size);

        Sort sort = sortDir.equalsIgnoreCase("DESC")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VehicleResponse> vehicles = vehicleService.getAllVehicles(pageable);

        return ResponseEntity.ok(ApiResponse.success(vehicles, "Vehicles retrieved successfully"));
    }

    /**
     * Update vehicle
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update vehicle", description = "Update vehicle information (partial update)")
    public ResponseEntity<ApiResponse<VehicleDetailResponse>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVehicleRequest request) {

        log.info("REST request to update vehicle: {}", id);

        VehicleDetailResponse response = vehicleService.updateVehicle(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Vehicle updated successfully"));
    }

    /**
     * Delete vehicle
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle", description = "Delete a vehicle and its associated state")
    public ResponseEntity<ApiResponse<Void>> deleteVehicle(@PathVariable Long id) {

        log.info("REST request to delete vehicle: {}", id);

        vehicleService.deleteVehicle(id);

        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted successfully"));
    }

    /**
     * Get vehicles by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get vehicles by status", description = "Filter vehicles by their current status")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehiclesByStatus(
            @PathVariable VehicleStatus status) {

        log.info("REST request to get vehicles by status: {}", status);

        List<VehicleResponse> vehicles = vehicleService.getVehiclesByStatus(status);

        return ResponseEntity.ok(ApiResponse.success(vehicles,
                String.format("Found %d vehicles with status %s", vehicles.size(), status)));
    }

    /**
     * Get vehicles by fleet hub
     */
    @GetMapping("/hub/{hubId}")
    @Operation(summary = "Get vehicles by fleet hub", description = "Get all vehicles in a specific fleet hub")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getVehiclesByHub(
            @PathVariable Long hubId) {

        log.info("REST request to get vehicles by hub: {}", hubId);

        List<VehicleResponse> vehicles = vehicleService.getVehiclesByFleetHub(hubId);

        return ResponseEntity.ok(ApiResponse.success(vehicles,
                String.format("Found %d vehicles in hub %d", vehicles.size(), hubId)));
    }
}
