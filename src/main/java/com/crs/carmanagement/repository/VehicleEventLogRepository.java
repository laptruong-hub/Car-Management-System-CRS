package com.crs.carmanagement.repository;

import com.crs.carmanagement.entity.VehicleEventLog;
import com.crs.carmanagement.enums.EventType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for VehicleEventLog entity (Option B)
 */
@Repository
public interface VehicleEventLogRepository extends JpaRepository<VehicleEventLog, Long> {

    /**
     * Find events by vehicle ID, ordered by occurrence time descending
     */
    List<VehicleEventLog> findByVehicleIdOrderByOccurredAtDesc(Long vehicleId);

    /**
     * Find events by vehicle ID with limit
     */
    List<VehicleEventLog> findByVehicleIdOrderByOccurredAtDesc(Long vehicleId, Pageable pageable);

    /**
     * Find events by event type
     */
    List<VehicleEventLog> findByEventType(EventType eventType);

    /**
     * Find events by vehicle and event type
     */
    List<VehicleEventLog> findByVehicleIdAndEventType(Long vehicleId, EventType eventType);

    /**
     * Find events in time range
     */
    List<VehicleEventLog> findByVehicleIdAndOccurredAtBetween(
            Long vehicleId,
            LocalDateTime startTime,
            LocalDateTime endTime);
}
