package com.crs.carmanagement.dto.response;

import com.crs.carmanagement.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for detailed vehicle information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetailResponse {

    // Basic vehicle info
    private Long id;
    private String plateNumber;
    private String vin;
    private String color;
    private Integer manufactureYear;
    private VehicleStatus status;
    private Double odometerKm;
    private Boolean isVirtual;

    // Model information
    private Long modelId;
    private String modelName;
    private String brand;
    private String modelSpecs;
    private Double batteryCapacityKwh;

    // Fleet hub information
    private Long fleetHubId;
    private String fleetHubName;
    private String fleetHubLocation;

    // Current state information
    private VehicleStateInfo currentState;

    // Option B: Integration fields
    private String currentBookingId;
    private String currentDriverId;

    // Recent events (Option B)
    private List<VehicleEventInfo> recentEvents;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested class for vehicle state information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleStateInfo {
        private Double latitude;
        private Double longitude;
        private Integer batteryLevel;
        private Boolean isCharging;
        private Double speedKmh;
        private LocalDateTime lastUpdatedAt;
        private String dataSource;
    }

    /**
     * Nested class for event information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VehicleEventInfo {
        private String eventType;
        private String eventData;
        private LocalDateTime occurredAt;
    }
}
