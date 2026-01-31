package com.crs.carmanagement.simulator;

import com.crs.carmanagement.config.SimulatorConfig;
import com.crs.carmanagement.dto.request.UpdateVehicleStateRequest;
import com.crs.carmanagement.entity.Vehicle;
import com.crs.carmanagement.enums.DataSource;
import com.crs.carmanagement.enums.VehicleStatus;
import com.crs.carmanagement.repository.VehicleRepository;
import com.crs.carmanagement.service.VehicleStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Virtual Car Simulator - Simulates real-time vehicle movement
 * Updates GPS, battery, speed automatically every few seconds
 * 
 * IMPORTANT: This is for TESTING/DEMO purposes only!
 * In production, real vehicles would send data via RabbitMQ
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VirtualCarSimulator {

    private final VehicleRepository vehicleRepository;
    private final VehicleStateService vehicleStateService;
    private final SimulatorConfig config;
    private final Random random = new Random();
    private final AtomicLong messageSequence = new AtomicLong(1);

    // Movement parameters
    private static final double LATITUDE_STEP = 0.001; // ~111 meters per step
    private static final double LONGITUDE_STEP = 0.001; // ~111 meters per step
    private static final double MAX_SPEED = 80.0; // km/h
    private static final double BATTERY_DRAIN_RATE = 0.5; // % per update when moving
    private static final double CHARGING_RATE = 2.0; // % per update when charging

    /**
     * Main simulation loop - runs based on configured interval
     * Updates configured virtual vehicles
     */
    @Scheduled(fixedRateString = "${simulator.update-interval-ms:5000}", initialDelay = 10000)
    public void simulateVehicles() {
        // Check if simulator is enabled
        if (!config.isEnabled()) {
            return;
        }

        try {
            // Get all virtual vehicles
            List<Vehicle> virtualVehicles = vehicleRepository.findByIsVirtual(true);

            // Filter by configured vehicle IDs
            List<Vehicle> vehiclesToSimulate = virtualVehicles.stream()
                    .filter(v -> config.shouldSimulate(v.getId()))
                    .toList();

            if (vehiclesToSimulate.isEmpty()) {
                return;
            }

            log.info("\n" + "=".repeat(80));
            log.info("🚗 SIMULATOR UPDATE | {} vehicles active | Interval: {}ms",
                    vehiclesToSimulate.size(), config.getUpdateIntervalMs());
            log.info("=".repeat(80));

            for (Vehicle vehicle : vehiclesToSimulate) {
                try {
                    simulateSingleVehicle(vehicle);
                } catch (Exception e) {
                    log.error("❌ Error simulating vehicle {}: {}", vehicle.getId(), e.getMessage());
                }
            }

            log.info("-".repeat(80) + "\n");

        } catch (Exception e) {
            log.error("❌ Error in vehicle simulation loop", e);
        }
    }

    /**
     * Simulate a single vehicle based on its current status
     */
    private void simulateSingleVehicle(Vehicle vehicle) {
        UpdateVehicleStateRequest request = new UpdateVehicleStateRequest();
        request.setDataSource(DataSource.VIRTUAL_CAR);
        request.setMessageSequence(messageSequence.getAndIncrement());

        switch (vehicle.getStatus()) {
            case IN_USE:
                simulateMovingVehicle(vehicle, request);
                break;

            case CHARGING:
                simulateChargingVehicle(vehicle, request);
                break;

            case AVAILABLE:
                simulateParkedVehicle(vehicle, request);
                break;

            case MAINTENANCE:
            case DAMAGED:
                // Don't simulate these statuses
                return;

            default:
                return;
        }

        // Update vehicle state
        vehicleStateService.updateVehicleState(vehicle.getId(), request);
    }

    /**
     * Simulate a vehicle that is currently moving (IN_USE)
     */
    private void simulateMovingVehicle(Vehicle vehicle, UpdateVehicleStateRequest request) {
        var currentState = vehicleStateService.getVehicleState(vehicle.getId());

        // Random movement direction
        double latChange = (random.nextBoolean() ? 1 : -1) * LATITUDE_STEP * random.nextDouble();
        double lonChange = (random.nextBoolean() ? 1 : -1) * LONGITUDE_STEP * random.nextDouble();

        double newLat = (currentState.getLatitude() != null ? currentState.getLatitude() : 10.762622) + latChange;
        double newLon = (currentState.getLongitude() != null ? currentState.getLongitude() : 106.660172) + lonChange;

        // Keep within HCM City bounds (approximately)
        newLat = Math.max(10.6, Math.min(10.9, newLat));
        newLon = Math.max(106.5, Math.min(106.9, newLon));

        request.setLatitude(newLat);
        request.setLongitude(newLon);

        // Random speed between 20-80 km/h
        double speed = 20 + random.nextDouble() * 60;
        request.setSpeedKmh(speed);

        // Drain battery based on speed
        int currentBattery = currentState.getBatteryLevel() != null ? currentState.getBatteryLevel() : 100;
        double batteryDrain = BATTERY_DRAIN_RATE * (speed / MAX_SPEED);
        int newBattery = Math.max(0, (int) (currentBattery - batteryDrain));
        request.setBatteryLevel(newBattery);
        request.setIsCharging(false);

        // CRITICAL: If battery reaches 0%, immediately switch to CHARGING
        if (newBattery == 0) {
            log.warn(" CRITICAL: Vehicle {} battery depleted! Forcing CHARGING status",
                    vehicle.getPlateNumber());
            vehicle.setStatus(VehicleStatus.CHARGING);
            vehicleRepository.save(vehicle);
        }

        // Increase odometer (5 seconds at current speed)
        double odometerIncrease = speed / 720.0; // km in 5 seconds
        double currentOdometer = currentState.getOdometerKm() != null ? currentState.getOdometerKm() : 0;
        request.setOdometerKm(currentOdometer + odometerIncrease);

        log.info("  🚙 {} [IN_USE] | GPS: ({}, {}) | Speed: {} km/h | Battery: {}% | Odo: {} km",
                vehicle.getPlateNumber(),
                String.format("%.4f", newLat),
                String.format("%.4f", newLon),
                String.format("%.1f", speed),
                newBattery,
                String.format("%.2f", request.getOdometerKm()));
    }

    /**
     * Simulate a vehicle that is charging
     */
    private void simulateChargingVehicle(Vehicle vehicle, UpdateVehicleStateRequest request) {
        var currentState = vehicleStateService.getVehicleState(vehicle.getId());

        // Vehicle doesn't move while charging
        request.setLatitude(currentState.getLatitude());
        request.setLongitude(currentState.getLongitude());
        request.setSpeedKmh(0.0);

        // Increase battery
        int currentBattery = currentState.getBatteryLevel() != null ? currentState.getBatteryLevel() : 0;
        int newBattery = Math.min(100, (int) (currentBattery + CHARGING_RATE));
        request.setBatteryLevel(newBattery);
        request.setIsCharging(true);

        // Keep odometer same
        request.setOdometerKm(currentState.getOdometerKm());

        log.info("  🔌 {} [CHARGING] | Battery: {}% → {}% (+{}%) | Speed: 0 km/h",
                vehicle.getPlateNumber(),
                currentBattery,
                newBattery,
                (newBattery - currentBattery));
    }

    /**
     * Simulate a parked/available vehicle (small battery variations)
     */
    private void simulateParkedVehicle(Vehicle vehicle, UpdateVehicleStateRequest request) {
        var currentState = vehicleStateService.getVehicleState(vehicle.getId());

        // Vehicle doesn't move
        request.setLatitude(currentState.getLatitude());
        request.setLongitude(currentState.getLongitude());
        request.setSpeedKmh(0.0);

        // Battery stays the same (no drain when parked)
        int currentBattery = currentState.getBatteryLevel() != null ? currentState.getBatteryLevel() : 100;
        request.setBatteryLevel(currentBattery); // No change!
        request.setIsCharging(false);

        // Odometer stays same
        request.setOdometerKm(currentState.getOdometerKm());

        log.info("  {} [AVAILABLE] | Battery: {}% (stable) | Parked at ({}, {})",
                vehicle.getPlateNumber(),
                currentBattery,
                String.format("%.4f", currentState.getLatitude()),
                String.format("%.4f", currentState.getLongitude()));
    }

    /**
     * Randomly change vehicle status to create realistic scenarios
     * Runs based on configured interval
     */
    @Scheduled(fixedRateString = "${simulator.status-change-interval-ms:30000}", initialDelay = 20000)
    public void randomStatusChanges() {
        if (!config.isEnabled()) {
            return;
        }

        try {
            List<Vehicle> virtualVehicles = vehicleRepository.findByIsVirtual(true);

            // Filter by configured vehicle IDs
            List<Vehicle> vehiclesToChange = virtualVehicles.stream()
                    .filter(v -> config.shouldSimulate(v.getId()))
                    .toList();

            for (Vehicle vehicle : vehiclesToChange) {
                // 5% chance of status change
                if (random.nextInt(100) < 5) {
                    changeVehicleStatus(vehicle);
                }
            }
        } catch (Exception e) {
            log.error("Error in random status change", e);
        }
    }

    /**
     * Randomly change vehicle status based on current state
     */
    private void changeVehicleStatus(Vehicle vehicle) {
        var currentState = vehicleStateService.getVehicleState(vehicle.getId());
        VehicleStatus newStatus = null;

        switch (vehicle.getStatus()) {
            case AVAILABLE:
                // CRITICAL: If battery is 0%, force charging immediately
                if (currentState.getBatteryLevel() == 0) {
                    newStatus = VehicleStatus.CHARGING;
                    log.warn(" CRITICAL: Vehicle {} battery at 0%! Status: AVAILABLE → CHARGING",
                            vehicle.getPlateNumber());
                }
                // Available → IN_USE (someone rented it) - only if battery > 20%
                else if (currentState.getBatteryLevel() > 20) {
                    newStatus = VehicleStatus.IN_USE;
                    log.info(" Vehicle {} rented! Status: AVAILABLE → IN_USE", vehicle.getPlateNumber());
                }
                break;

            case IN_USE:
                // CRITICAL: If battery is 0%, force charging immediately
                if (currentState.getBatteryLevel() == 0) {
                    newStatus = VehicleStatus.CHARGING;
                    log.warn(" CRITICAL: Vehicle {} battery at 0%! Forcing CHARGING status",
                            vehicle.getPlateNumber());
                }
                // If battery < 30%, needs charging
                else if (currentState.getBatteryLevel() < 30) {
                    newStatus = VehicleStatus.CHARGING;
                    log.info(" Vehicle {} needs charging! Status: IN_USE → CHARGING",
                            vehicle.getPlateNumber());
                }
                // Random trip completion
                else if (random.nextBoolean()) {
                    newStatus = VehicleStatus.AVAILABLE;
                    log.info(" Vehicle {} returned! Status: IN_USE → AVAILABLE",
                            vehicle.getPlateNumber());
                }
                break;

            case CHARGING:
                // CHARGING → AVAILABLE when fully charged
                if (currentState.getBatteryLevel() >= 95) {
                    newStatus = VehicleStatus.AVAILABLE;
                    log.info("⚡ Vehicle {} fully charged! Status: CHARGING → AVAILABLE",
                            vehicle.getPlateNumber());
                }
                break;

            default:
                return;
        }

        if (newStatus != null) {
            vehicle.setStatus(newStatus);
            vehicleRepository.save(vehicle);
        }
    }
}
