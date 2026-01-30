package com.crs.carmanagement.entity;

import com.crs.carmanagement.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Vehicle Event Log entity - Option B implementation for audit trail
 * Stores all vehicle-related events with flexible JSONB data
 */
@Entity
@Table(name = "vehicle_event_log", indexes = {
        @Index(name = "idx_vehicle_events", columnList = "vehicle_id, occurred_at DESC"),
        @Index(name = "idx_event_type", columnList = "event_type"),
        @Index(name = "idx_occurred_at", columnList = "occurred_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Column(name = "event_data", columnDefinition = "TEXT")
    private String eventData; // JSON as String for simplicity

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
