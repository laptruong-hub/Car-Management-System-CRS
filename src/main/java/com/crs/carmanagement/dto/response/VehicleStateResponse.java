package com.crs.carmanagement.dto.response;

import com.crs.carmanagement.enums.DataSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for vehicle state information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Vehicle state information")
public class VehicleStateResponse {

    @Schema(description = "Vehicle ID", example = "1")
    private Long vehicleId;

    @Schema(description = "Vehicle plate number", example = "29A-12345")
    private String plateNumber;

    @Schema(description = "Latitude", example = "10.762622")
    private Double latitude;

    @Schema(description = "Longitude", example = "106.660172")
    private Double longitude;

    @Schema(description = "Battery level (%)", example = "85")
    private Integer batteryLevel;

    @Schema(description = "Is charging", example = "false")
    private Boolean isCharging;

    @Schema(description = "Speed (km/h)", example = "45.5")
    private Double speedKmh;

    @Schema(description = "Odometer (km)", example = "1250.5")
    private Double odometerKm;

    @Schema(description = "Last updated time", example = "2026-01-30T14:20:00")
    private LocalDateTime lastUpdatedAt;

    @Schema(description = "Data source", example = "MANUAL")
    private DataSource dataSource;

    @Schema(description = "Message sequence", example = "125")
    private Long messageSequence;

    @Schema(description = "State created time", example = "2026-01-30T13:52:15")
    private LocalDateTime createdAt;

    @Schema(description = "State last modified time", example = "2026-01-30T14:20:00")
    private LocalDateTime updatedAt;
}
