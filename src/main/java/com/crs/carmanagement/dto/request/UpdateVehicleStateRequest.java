package com.crs.carmanagement.dto.request;

import com.crs.carmanagement.enums.DataSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating vehicle state (GPS, battery, speed)
 * Used for manual updates and will be reused by RabbitMQ consumer later
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update vehicle state")
public class UpdateVehicleStateRequest {

    @Schema(description = "Latitude coordinate", example = "10.762622")
    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @Schema(description = "Longitude coordinate", example = "106.660172")
    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    @Schema(description = "Battery level percentage (0-100)", example = "85")
    @Min(value = 0, message = "Battery level must be between 0 and 100")
    @Max(value = 100, message = "Battery level must be between 0 and 100")
    private Integer batteryLevel;

    @Schema(description = "Is vehicle charging", example = "false")
    private Boolean isCharging;

    @Schema(description = "Current speed in km/h", example = "45.5")
    @DecimalMin(value = "0.0", message = "Speed cannot be negative")
    @DecimalMax(value = "200.0", message = "Speed cannot exceed 200 km/h")
    private Double speedKmh;

    @Schema(description = "Current odometer reading in km", example = "1250.5")
    @DecimalMin(value = "0.0", message = "Odometer cannot be negative")
    private Double odometerKm;

    @Schema(description = "Data source (MANUAL, VIRTUAL_CAR, SYSTEM)", example = "MANUAL")
    private DataSource dataSource;

    @Schema(description = "Message sequence number for ordering", example = "125")
    @Min(value = 0, message = "Message sequence must be non-negative")
    private Long messageSequence;
}
