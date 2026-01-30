package com.crs.carmanagement.controller;

import com.crs.carmanagement.dto.request.UpdateVehicleStateRequest;
import com.crs.carmanagement.dto.response.ApiResponse;
import com.crs.carmanagement.dto.response.VehicleStateResponse;
import com.crs.carmanagement.service.VehicleStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for vehicle state operations (GPS tracking, battery
 * monitoring)
 */
@RestController
@RequestMapping("/api/v1/vehicles/{vehicleId}/state")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicle State", description = "Vehicle state management APIs (GPS, battery, speed)")
public class VehicleStateController {

    private final VehicleStateService vehicleStateService;

    /**
     * Get current vehicle state
     */
    @GetMapping
    @Operation(summary = "Get vehicle state", description = "Get current GPS location, battery level, and other state information")
    public ResponseEntity<ApiResponse<VehicleStateResponse>> getVehicleState(
            @PathVariable Long vehicleId) {

        log.info("REST request to get state for vehicle: {}", vehicleId);

        VehicleStateResponse response = vehicleStateService.getVehicleState(vehicleId);

        return ResponseEntity.ok(ApiResponse.success(response, "Vehicle state retrieved successfully"));
    }

    /**
     * Update vehicle state (GPS, battery, speed, etc.)
     * This can be called manually or will be used by RabbitMQ consumer in the
     * future
     */
    @PutMapping
    @Operation(summary = "Update vehicle state", description = "Update vehicle GPS location, battery level, speed, and other state data. "
            +
            "Supports partial updates - only send fields you want to change.")
    public ResponseEntity<ApiResponse<VehicleStateResponse>> updateVehicleState(
            @PathVariable Long vehicleId,
            @Valid @RequestBody UpdateVehicleStateRequest request) {

        log.info("REST request to update state for vehicle: {}", vehicleId);

        VehicleStateResponse response = vehicleStateService.updateVehicleState(vehicleId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Vehicle state updated successfully"));
    }

    /**
     * Convenience endpoint to update only GPS location
     */
    @PutMapping("/location")
    @Operation(summary = "Update GPS location", description = "Quick update for GPS coordinates only")
    public ResponseEntity<ApiResponse<VehicleStateResponse>> updateLocation(
            @PathVariable Long vehicleId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        log.info("REST request to update location for vehicle: {} to ({}, {})",
                vehicleId, latitude, longitude);

        UpdateVehicleStateRequest request = UpdateVehicleStateRequest.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();

        VehicleStateResponse response = vehicleStateService.updateVehicleState(vehicleId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "GPS location updated successfully"));
    }

    /**
     * Convenience endpoint to update battery level
     */
    @PutMapping("/battery")
    @Operation(summary = "Update battery level", description = "Quick update for battery percentage and charging status")
    public ResponseEntity<ApiResponse<VehicleStateResponse>> updateBattery(
            @PathVariable Long vehicleId,
            @RequestParam Integer batteryLevel,
            @RequestParam(required = false) Boolean isCharging) {

        log.info("REST request to update battery for vehicle: {} to {}%", vehicleId, batteryLevel);

        UpdateVehicleStateRequest request = UpdateVehicleStateRequest.builder()
                .batteryLevel(batteryLevel)
                .isCharging(isCharging)
                .build();

        VehicleStateResponse response = vehicleStateService.updateVehicleState(vehicleId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Battery level updated successfully"));
    }
}
