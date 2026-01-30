package com.crs.carmanagement.dto.request;

import com.crs.carmanagement.enums.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing vehicle
 * All fields are optional for partial updates
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    private VehicleStatus status;

    @DecimalMin(value = "0.0", message = "Odometer cannot be negative")
    private Double odometerKm;

    private Long fleetHubId;

    @Size(max = 50, message = "Current booking ID must not exceed 50 characters")
    private String currentBookingId;

    @Size(max = 50, message = "Current driver ID must not exceed 50 characters")
    private String currentDriverId;
}
