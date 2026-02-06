package com.crs.carmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Fleet Hub entity - represents a physical location where vehicles are
 * stationed
 */
@Entity
@Table(name = "fleet_hub")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FleetHub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String location;

    // GPS coordinates for the hub
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "current_occupancy", nullable = false)
    @Builder.Default
    private Integer currentOccupancy = 0;

    @Column(name = "has_charging_station")
    @Builder.Default
    private Boolean hasChargingStation = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
