package com.crs.carmanagement.entity;

import com.crs.carmanagement.enums.DataSource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Vehicle State entity - tracks current real-time state of a vehicle
 * Option B enhancements: lastUpdatedAt, dataSource, messageSequence
 */
@Entity
@Table(name = "vehicle_state", indexes = {
        @Index(name = "idx_vehicle_state_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_last_updated", columnList = "last_updated_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    private Vehicle vehicle;

    // Location tracking
    @Column(name = "latitude", columnDefinition = "DOUBLE PRECISION")
    private Double latitude;

    @Column(name = "longitude", columnDefinition = "DOUBLE PRECISION")
    private Double longitude;

    // Battery information
    @Column(name = "battery_level")
    private Integer batteryLevel; // Percentage (0-100)

    @Column(name = "is_charging")
    @Builder.Default
    private Boolean isCharging = false;

    // Movement metrics
    @Column(name = "speed_kmh")
    private Double speedKmh;

    @Column(name = "odometer_km")
    private Double odometerKm;

    // Option B: Tracking fields
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_source", length = 20)
    @Builder.Default
    private DataSource dataSource = DataSource.SYSTEM;

    @Column(name = "message_sequence")
    private Long messageSequence;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
