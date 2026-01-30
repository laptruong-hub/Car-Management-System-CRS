package com.crs.carmanagement.service.impl;

import com.crs.carmanagement.dto.request.UpdateVehicleStateRequest;
import com.crs.carmanagement.dto.response.VehicleStateResponse;
import com.crs.carmanagement.entity.Vehicle;
import com.crs.carmanagement.entity.VehicleState;
import com.crs.carmanagement.enums.DataSource;
import com.crs.carmanagement.enums.EventType;
import com.crs.carmanagement.exception.BusinessValidationException;
import com.crs.carmanagement.exception.EntityNotFoundException;
import com.crs.carmanagement.repository.VehicleRepository;
import com.crs.carmanagement.repository.VehicleStateRepository;
import com.crs.carmanagement.service.VehicleEventLogService;
import com.crs.carmanagement.service.VehicleStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of vehicle state service
 * Handles GPS tracking, battery monitoring, and state updates
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleStateServiceImpl implements VehicleStateService {

    private final VehicleRepository vehicleRepository;
    private final VehicleStateRepository vehicleStateRepository;
    private final VehicleEventLogService eventLogService;

    @Override
    @Transactional
    public VehicleStateResponse updateVehicleState(Long vehicleId, UpdateVehicleStateRequest request) {
        log.info("Updating state for vehicle ID: {}", vehicleId);

        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));

        // Get or create vehicle state
        VehicleState state = vehicleStateRepository.findByVehicleId(vehicleId)
                .orElseGet(() -> {
                    log.info("Creating new state for vehicle: {}", vehicleId);
                    return VehicleState.builder()
                            .vehicle(vehicle)
                            .batteryLevel(100)
                            .isCharging(false)
                            .speedKmh(0.0)
                            .odometerKm(vehicle.getOdometerKm())
                            .dataSource(DataSource.SYSTEM)
                            .messageSequence(0L)
                            .build();
                });

        // Track changes for event logging
        Map<String, Object> changes = new HashMap<>();

        // Update fields if provided
        if (request.getLatitude() != null) {
            if (!request.getLatitude().equals(state.getLatitude())) {
                changes.put("latitude", Map.of("old", state.getLatitude(), "new", request.getLatitude()));
            }
            state.setLatitude(request.getLatitude());
        }

        if (request.getLongitude() != null) {
            if (!request.getLongitude().equals(state.getLongitude())) {
                changes.put("longitude", Map.of("old", state.getLongitude(), "new", request.getLongitude()));
            }
            state.setLongitude(request.getLongitude());
        }

        if (request.getBatteryLevel() != null) {
            if (!request.getBatteryLevel().equals(state.getBatteryLevel())) {
                changes.put("batteryLevel", Map.of("old", state.getBatteryLevel(), "new", request.getBatteryLevel()));
            }
            state.setBatteryLevel(request.getBatteryLevel());
        }

        if (request.getIsCharging() != null) {
            if (!request.getIsCharging().equals(state.getIsCharging())) {
                changes.put("isCharging", Map.of("old", state.getIsCharging(), "new", request.getIsCharging()));
            }
            state.setIsCharging(request.getIsCharging());
        }

        if (request.getSpeedKmh() != null) {
            state.setSpeedKmh(request.getSpeedKmh());
        }

        if (request.getOdometerKm() != null) {
            // Validate odometer only increases
            if (state.getOdometerKm() != null && request.getOdometerKm() < state.getOdometerKm()) {
                throw new BusinessValidationException(
                        "Odometer cannot decrease. Current: " + state.getOdometerKm() + ", New: "
                                + request.getOdometerKm());
            }
            if (!request.getOdometerKm().equals(state.getOdometerKm())) {
                changes.put("odometerKm", Map.of("old", state.getOdometerKm(), "new", request.getOdometerKm()));
            }
            state.setOdometerKm(request.getOdometerKm());

            // Also update vehicle's odometer
            vehicle.setOdometerKm(request.getOdometerKm());
            vehicleRepository.save(vehicle);
        }

        // Update Option B tracking fields
        state.setLastUpdatedAt(LocalDateTime.now());

        if (request.getDataSource() != null) {
            state.setDataSource(request.getDataSource());
        } else {
            // Default to MANUAL if not specified
            state.setDataSource(DataSource.MANUAL);
        }

        if (request.getMessageSequence() != null) {
            // Only validate message sequence for real external sources (RabbitMQ)
            // Skip validation for VIRTUAL_CAR simulator to allow restarts
            boolean isExternalSource = request.getDataSource() != null
                    && request.getDataSource() != DataSource.VIRTUAL_CAR
                    && request.getDataSource() != DataSource.MANUAL;

            if (isExternalSource && state.getMessageSequence() != null
                    && request.getMessageSequence() <= state.getMessageSequence()) {
                log.warn("Ignoring out-of-order message. Current: {}, New: {}",
                        state.getMessageSequence(), request.getMessageSequence());
                throw new BusinessValidationException(
                        "Message sequence must be greater than current sequence: " + state.getMessageSequence());
            }
            state.setMessageSequence(request.getMessageSequence());
        }

        // Save state
        state = vehicleStateRepository.save(state);

        // Log event if there were significant changes (Option B)
        if (!changes.isEmpty()) {
            changes.put("dataSource", state.getDataSource().name());
            if (state.getMessageSequence() != null) {
                changes.put("messageSequence", state.getMessageSequence());
            }
            eventLogService.logEvent(vehicleId, EventType.STATE_UPDATED, changes);
        }

        log.info("Vehicle state updated successfully for vehicle: {}", vehicleId);
        return buildStateResponse(vehicle, state);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleStateResponse getVehicleState(Long vehicleId) {
        log.debug("Fetching state for vehicle ID: {}", vehicleId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", vehicleId));

        VehicleState state = vehicleStateRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new EntityNotFoundException("VehicleState not found for vehicle: " + vehicleId));

        return buildStateResponse(vehicle, state);
    }

    /**
     * Build state response DTO from entity
     */
    private VehicleStateResponse buildStateResponse(Vehicle vehicle, VehicleState state) {
        return VehicleStateResponse.builder()
                .vehicleId(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .latitude(state.getLatitude())
                .longitude(state.getLongitude())
                .batteryLevel(state.getBatteryLevel())
                .isCharging(state.getIsCharging())
                .speedKmh(state.getSpeedKmh())
                .odometerKm(state.getOdometerKm())
                .lastUpdatedAt(state.getLastUpdatedAt())
                .dataSource(state.getDataSource())
                .messageSequence(state.getMessageSequence())
                .createdAt(state.getCreatedAt())
                .updatedAt(state.getUpdatedAt())
                .build();
    }
}
