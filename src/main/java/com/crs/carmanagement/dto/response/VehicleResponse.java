package com.crs.carmanagement.dto.response;

import com.crs.carmanagement.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for vehicle summary (used in list views)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private Long id;
    private String plateNumber;
    private String modelName;
    private String brand;
    private String color;
    private VehicleStatus status;
    private Boolean isVirtual;
    private Integer batteryLevel;
    private String fleetHubName;
    private Double odometerKm;
}
