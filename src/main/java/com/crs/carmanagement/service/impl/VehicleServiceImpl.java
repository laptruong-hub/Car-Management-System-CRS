package com.crs.carmanagement.service.impl;

import com.crs.carmanagement.dto.request.CreateVehicleRequest;
import com.crs.carmanagement.dto.request.UpdateVehicleRequest;
import com.crs.carmanagement.dto.response.VehicleDetailResponse;
import com.crs.carmanagement.dto.response.VehicleResponse;
import com.crs.carmanagement.entity.*;
import com.crs.carmanagement.enums.DataSource;
import com.crs.carmanagement.enums.EventType;
import com.crs.carmanagement.enums.VehicleStatus;
import com.crs.carmanagement.exception.BusinessValidationException;
import com.crs.carmanagement.exception.EntityNotFoundException;
import com.crs.carmanagement.repository.*;
import com.crs.carmanagement.service.VehicleEventLogService;
import com.crs.carmanagement.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of VehicleService with Option B event logging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleStateRepository vehicleStateRepository;
    private final FleetHubRepository fleetHubRepository;
    private final VehicleEventLogService eventLogService;

    @Override
    @Transactional
    public VehicleDetailResponse createVehicle(CreateVehicleRequest request) {
        log.info("Creating vehicle with plate number: {}", request.getPlateNumber());

        // Validate plate number uniqueness
        if (vehicleRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new BusinessValidationException(
                    "Vehicle with plate number " + request.getPlateNumber() + " already exists");
        }

        // Validate VIN uniqueness
        if (vehicleRepository.existsByVin(request.getVin())) {
            throw new BusinessValidationException(
                    "Vehicle with VIN " + request.getVin() + " already exists");
        }

        // Validate model exists
        VehicleModel model = vehicleModelRepository.findById(request.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("VehicleModel", request.getModelId()));

        // Validate fleet hub exists
        FleetHub fleetHub = fleetHubRepository.findById(request.getFleetHubId())
                .orElseThrow(() -> new EntityNotFoundException("FleetHub", request.getFleetHubId()));

        // Create vehicle
        Vehicle vehicle = Vehicle.builder()
                .model(model)
                .plateNumber(request.getPlateNumber())
                .vin(request.getVin())
                .color(request.getColor())
                .manufactureYear(request.getManufactureYear())
                .status(VehicleStatus.AVAILABLE)
                .odometerKm(request.getOdometerKm())
                .fleetHub(fleetHub)
                .isVirtual(request.getIsVirtual())
                .build();

        vehicle = vehicleRepository.save(vehicle);

        // Create initial vehicle state
        VehicleState state = VehicleState.builder()
                .vehicle(vehicle)
                .batteryLevel(100) // Default to full battery
                .isCharging(false)
                .speedKmh(0.0)
                .odometerKm(request.getOdometerKm())
                .lastUpdatedAt(LocalDateTime.now())
                .dataSource(DataSource.SYSTEM)
                .messageSequence(0L)
                .build();

        vehicleStateRepository.save(state);

        // Log event (Option B)
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("plateNumber", vehicle.getPlateNumber());
        eventData.put("vin", vehicle.getVin());
        eventData.put("modelId", model.getId());
        eventData.put("isVirtual", vehicle.getIsVirtual());
        eventLogService.logEvent(vehicle.getId(), EventType.VEHICLE_CREATED, eventData);

        log.info("Vehicle created successfully with ID: {}", vehicle.getId());
        return buildDetailResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleDetailResponse getVehicleById(Long id) {
        log.debug("Fetching vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findByIdWithModel(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));

        return buildDetailResponse(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getAllVehicles(Pageable pageable) {
        log.debug("Fetching all vehicles with pagination: {}", pageable);

        Page<Vehicle> vehiclePage = vehicleRepository.findAll(pageable);
        return vehiclePage.map(this::buildSummaryResponse);
    }

    @Override
    @Transactional
    public VehicleDetailResponse updateVehicle(Long id, UpdateVehicleRequest request) {
        log.info("Updating vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));

        Map<String, Object> changes = new HashMap<>();

        // Update fields if provided
        if (request.getColor() != null && !request.getColor().equals(vehicle.getColor())) {
            changes.put("color", Map.of("old", vehicle.getColor(), "new", request.getColor()));
            vehicle.setColor(request.getColor());
        }

        if (request.getStatus() != null && !request.getStatus().equals(vehicle.getStatus())) {
            changes.put("status", Map.of("old", vehicle.getStatus(), "new", request.getStatus()));
            vehicle.setStatus(request.getStatus());
        }

        if (request.getOdometerKm() != null && !request.getOdometerKm().equals(vehicle.getOdometerKm())) {
            changes.put("odometerKm", Map.of("old", vehicle.getOdometerKm(), "new", request.getOdometerKm()));
            vehicle.setOdometerKm(request.getOdometerKm());
        }

        if (request.getFleetHubId() != null) {
            FleetHub newHub = fleetHubRepository.findById(request.getFleetHubId())
                    .orElseThrow(() -> new EntityNotFoundException("FleetHub", request.getFleetHubId()));

            if (!newHub.getId().equals(vehicle.getFleetHub().getId())) {
                changes.put("fleetHubId", Map.of("old", vehicle.getFleetHub().getId(), "new", newHub.getId()));
                vehicle.setFleetHub(newHub);
            }
        }

        if (request.getCurrentBookingId() != null) {
            changes.put("currentBookingId", request.getCurrentBookingId());
            vehicle.setCurrentBookingId(request.getCurrentBookingId());
        }

        if (request.getCurrentDriverId() != null) {
            changes.put("currentDriverId", request.getCurrentDriverId());
            vehicle.setCurrentDriverId(request.getCurrentDriverId());
        }

        vehicle = vehicleRepository.save(vehicle);

        // Log event if there were changes
        if (!changes.isEmpty()) {
            eventLogService.logEvent(vehicle.getId(), EventType.VEHICLE_UPDATED, changes);
        }

        log.info("Vehicle updated successfully: {}", id);
        return buildDetailResponse(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        log.info("Deleting vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle", id));

        // Log event before deletion
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("plateNumber", vehicle.getPlateNumber());
        eventData.put("vin", vehicle.getVin());
        eventLogService.logEvent(vehicle.getId(), EventType.VEHICLE_DELETED, eventData);

        // Delete vehicle state
        vehicleStateRepository.deleteByVehicleId(id);

        // Delete vehicle
        vehicleRepository.delete(vehicle);

        log.info("Vehicle deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByStatus(VehicleStatus status) {
        log.debug("Fetching vehicles with status: {}", status);

        List<Vehicle> vehicles = vehicleRepository.findByStatus(status);
        return vehicles.stream()
                .map(this::buildSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getVehiclesByFleetHub(Long hubId) {
        log.debug("Fetching vehicles for fleet hub: {}", hubId);

        // Validate hub exists
        if (!fleetHubRepository.existsById(hubId)) {
            throw new EntityNotFoundException("FleetHub", hubId);
        }

        List<Vehicle> vehicles = vehicleRepository.findByFleetHubId(hubId);
        return vehicles.stream()
                .map(this::buildSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Build detailed response with all related data
     */
    private VehicleDetailResponse buildDetailResponse(Vehicle vehicle) {
        // Fetch state
        VehicleState state = vehicleStateRepository.findByVehicleId(vehicle.getId())
                .orElse(null);

        // Fetch recent events (last 5)
        List<VehicleEventLog> recentEvents = eventLogService.getVehicleEvents(vehicle.getId(), 5);

        return VehicleDetailResponse.builder()
                // Basic info
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .vin(vehicle.getVin())
                .color(vehicle.getColor())
                .manufactureYear(vehicle.getManufactureYear())
                .status(vehicle.getStatus())
                .odometerKm(vehicle.getOdometerKm())
                .isVirtual(vehicle.getIsVirtual())

                // Model info
                .modelId(vehicle.getModel().getId())
                .modelName(vehicle.getModel().getModelName())
                .brand(vehicle.getModel().getBrand())
                .modelSpecs(vehicle.getModel().getSpecs())
                .batteryCapacityKwh(vehicle.getModel().getBatteryCapacityKwh())

                // Fleet hub info
                .fleetHubId(vehicle.getFleetHub() != null ? vehicle.getFleetHub().getId() : null)
                .fleetHubName(vehicle.getFleetHub() != null ? vehicle.getFleetHub().getName() : null)
                .fleetHubLocation(vehicle.getFleetHub() != null ? vehicle.getFleetHub().getLocation() : null)

                // Current state
                .currentState(state != null ? VehicleDetailResponse.VehicleStateInfo.builder()
                        .latitude(state.getLatitude())
                        .longitude(state.getLongitude())
                        .batteryLevel(state.getBatteryLevel())
                        .isCharging(state.getIsCharging())
                        .speedKmh(state.getSpeedKmh())
                        .lastUpdatedAt(state.getLastUpdatedAt())
                        .dataSource(state.getDataSource() != null ? state.getDataSource().name() : null)
                        .build() : null)

                // Integration fields
                .currentBookingId(vehicle.getCurrentBookingId())
                .currentDriverId(vehicle.getCurrentDriverId())

                // Recent events
                .recentEvents(recentEvents.stream()
                        .map(event -> VehicleDetailResponse.VehicleEventInfo.builder()
                                .eventType(event.getEventType().name())
                                .eventData(event.getEventData())
                                .occurredAt(event.getOccurredAt())
                                .build())
                        .collect(Collectors.toList()))

                // Timestamps
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    /**
     * Build summary response for list views
     */
    private VehicleResponse buildSummaryResponse(Vehicle vehicle) {
        VehicleState state = vehicleStateRepository.findByVehicleId(vehicle.getId())
                .orElse(null);

        return VehicleResponse.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .modelName(vehicle.getModel().getModelName())
                .brand(vehicle.getModel().getBrand())
                .color(vehicle.getColor())
                .status(vehicle.getStatus())
                .isVirtual(vehicle.getIsVirtual())
                .batteryLevel(state != null ? state.getBatteryLevel() : null)
                .fleetHubName(vehicle.getFleetHub() != null ? vehicle.getFleetHub().getName() : null)
                .odometerKm(vehicle.getOdometerKm())
                .build();
    }
}
