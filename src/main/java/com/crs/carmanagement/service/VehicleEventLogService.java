package com.crs.carmanagement.service;

import com.crs.carmanagement.entity.VehicleEventLog;
import com.crs.carmanagement.enums.EventType;
import com.crs.carmanagement.repository.VehicleEventLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for logging vehicle events (Option B implementation)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleEventLogService {

    private final VehicleEventLogRepository eventLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log an event for a vehicle
     */
    @Transactional
    public void logEvent(Long vehicleId, EventType eventType, Object eventData) {
        try {
            String eventDataJson = eventData != null
                    ? objectMapper.writeValueAsString(eventData)
                    : null;

            VehicleEventLog event = VehicleEventLog.builder()
                    .vehicleId(vehicleId)
                    .eventType(eventType)
                    .eventData(eventDataJson)
                    .occurredAt(LocalDateTime.now())
                    .build();

            eventLogRepository.save(event);
            log.debug("Logged event {} for vehicle {}", eventType, vehicleId);

        } catch (Exception e) {
            log.error("Failed to log event {} for vehicle {}: {}",
                    eventType, vehicleId, e.getMessage());
            // Don't throw exception - event logging should not break main flow
        }
    }

    /**
     * Get recent events for a vehicle
     */
    public List<VehicleEventLog> getVehicleEvents(Long vehicleId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return eventLogRepository.findByVehicleIdOrderByOccurredAtDesc(vehicleId, pageRequest);
    }

    /**
     * Get all events for a vehicle
     */
    public List<VehicleEventLog> getVehicleEvents(Long vehicleId) {
        return eventLogRepository.findByVehicleIdOrderByOccurredAtDesc(vehicleId);
    }
}
