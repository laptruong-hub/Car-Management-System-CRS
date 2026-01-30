package com.crs.carmanagement.service;

import com.crs.carmanagement.dto.request.UpdateVehicleStateRequest;
import com.crs.carmanagement.dto.response.VehicleStateResponse;

/**
 * Service interface for vehicle state management
 */
public interface VehicleStateService {

    /**
     * Update vehicle state (GPS, battery, speed, etc.)
     * This method will be used by both manual API calls and RabbitMQ consumer
     * 
     * @param vehicleId the vehicle ID
     * @param request   the state update request
     * @return updated vehicle state response
     */
    VehicleStateResponse updateVehicleState(Long vehicleId, UpdateVehicleStateRequest request);

    /**
     * Get current vehicle state
     * 
     * @param vehicleId the vehicle ID
     * @return vehicle state response
     */
    VehicleStateResponse getVehicleState(Long vehicleId);
}
