package com.crs.carmanagement.dto.response;

import com.crs.carmanagement.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private String color;
    private VehicleStatus status;
    private Boolean isVirtual;
    private Double odometerKm;

    // Nested model information
    private ModelInfo model;

    // Nested current state information
    private StateInfo currentState;

    // Fleet hub name for display
    private String fleetHubName;

    /**
     * Nested class for model information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelInfo {
        private Long id;
        private String name;
        private String brand;
    }

    /**
     * Nested class for current state information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StateInfo {
        private Double latitude;
        private Double longitude;
        private Integer batteryLevel;
        private Boolean isCharging;
        private Double speedKmh;
        private LocalDateTime lastUpdatedAt;
    }
}
