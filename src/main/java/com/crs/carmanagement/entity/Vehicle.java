package com.crs.carmanagement.entity;

import com.crs.carmanagement.enums.VehicleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Vehicle entity - represents a physical/virtual car in the fleet
 * Includes Option B enhancements: currentBookingId, currentDriverId, isVirtual
 */
@Entity
@Table(name = "vehicle", indexes = {
        @Index(name = "idx_plate_number", columnList = "plate_number"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_is_virtual", columnList = "is_virtual")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private VehicleModel model;

    @Column(name = "plate_number", unique = true, nullable = false, length = 20)
    private String plateNumber;

    @Column(nullable = false, unique = true, length = 50)
    private String vin;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(name = "manufacture_year", nullable = false)
    private Integer manufactureYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "odometer_km")
    @Builder.Default
    private Double odometerKm = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fleet_hub_id")
    private FleetHub fleetHub;

    // Option B: Cache fields for integration with other services
    @Column(name = "current_booking_id", length = 50)
    private String currentBookingId;

    @Column(name = "current_driver_id", length = 50)
    private String currentDriverId;

    @Column(name = "is_virtual", nullable = false)
    @Builder.Default
    private Boolean isVirtual = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
