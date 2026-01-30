package com.crs.carmanagement.config;

import com.crs.carmanagement.entity.FleetHub;
import com.crs.carmanagement.entity.Vehicle;
import com.crs.carmanagement.entity.VehicleModel;
import com.crs.carmanagement.entity.VehicleState;
import com.crs.carmanagement.enums.DataSource;
import com.crs.carmanagement.enums.VehicleStatus;
import com.crs.carmanagement.repository.FleetHubRepository;
import com.crs.carmanagement.repository.VehicleModelRepository;
import com.crs.carmanagement.repository.VehicleRepository;
import com.crs.carmanagement.repository.VehicleStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Data initializer to populate database with sample data
 * Runs automatically when application starts
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final FleetHubRepository fleetHubRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleStateRepository vehicleStateRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            log.info("=== Starting Data Initialization ===");

            // Check if data already exists
            if (fleetHubRepository.count() > 0) {
                log.info("Data already exists. Skipping initialization.");
                return;
            }

            // Create Fleet Hubs
            log.info("Creating fleet hubs...");
            FleetHub hub1 = FleetHub.builder()
                    .name("District 1 Hub")
                    .location("Ho Chi Minh City, District 1")
                    .capacity(50)
                    .currentOccupancy(0)
                    .hasChargingStation(true)
                    .build();

            FleetHub hub2 = FleetHub.builder()
                    .name("District 7 Hub")
                    .location("Ho Chi Minh City, District 7")
                    .capacity(30)
                    .currentOccupancy(0)
                    .hasChargingStation(true)
                    .build();

            FleetHub hub3 = FleetHub.builder()
                    .name("Tan Binh Hub")
                    .location("Ho Chi Minh City, Tan Binh")
                    .capacity(40)
                    .currentOccupancy(0)
                    .hasChargingStation(false)
                    .build();

            hub1 = fleetHubRepository.save(hub1);
            hub2 = fleetHubRepository.save(hub2);
            hub3 = fleetHubRepository.save(hub3);
            log.info("Created {} fleet hubs", fleetHubRepository.count());

            // Create Vehicle Models
            log.info("Creating vehicle models...");
            VehicleModel model1 = VehicleModel.builder()
                    .modelId("VINFAST-VFE34")
                    .modelName("VinFast VF e34")
                    .brand("VinFast")
                    .manufacturerId("VF-001")
                    .active(true)
                    .specs("Electric SUV, 110kW motor, 300km range")
                    .imageUrl("https://example.com/vf-e34.jpg")
                    .batteryCapacityKwh(42.0)
                    .build();

            VehicleModel model2 = VehicleModel.builder()
                    .modelId("VINFAST-VF8")
                    .modelName("VinFast VF 8")
                    .brand("VinFast")
                    .manufacturerId("VF-002")
                    .active(true)
                    .specs("Electric SUV, 150kW motor, 420km range")
                    .imageUrl("https://example.com/vf-8.jpg")
                    .batteryCapacityKwh(87.7)
                    .build();

            VehicleModel model3 = VehicleModel.builder()
                    .modelId("TESLA-MODEL3")
                    .modelName("Tesla Model 3")
                    .brand("Tesla")
                    .manufacturerId("TESLA-001")
                    .active(true)
                    .specs("Electric Sedan, 211kW motor, 491km range")
                    .imageUrl("https://example.com/tesla-model3.jpg")
                    .batteryCapacityKwh(60.0)
                    .build();

            model1 = vehicleModelRepository.save(model1);
            model2 = vehicleModelRepository.save(model2);
            model3 = vehicleModelRepository.save(model3);
            log.info("Created {} vehicle models", vehicleModelRepository.count());

            // Create Vehicles
            log.info("Creating vehicles...");

            // Vehicle 1 - Virtual, Available
            Vehicle vehicle1 = Vehicle.builder()
                    .model(model1)
                    .plateNumber("29A-12345")
                    .vin("VIN1234567890001")
                    .color("White")
                    .manufactureYear(2024)
                    .status(VehicleStatus.AVAILABLE)
                    .odometerKm(1250.5)
                    .fleetHub(hub1)
                    .isVirtual(true)
                    .build();

            // Vehicle 2 - Virtual, In Use
            Vehicle vehicle2 = Vehicle.builder()
                    .model(model1)
                    .plateNumber("29B-67890")
                    .vin("VIN1234567890002")
                    .color("Black")
                    .manufactureYear(2024)
                    .status(VehicleStatus.IN_USE)
                    .odometerKm(3456.8)
                    .fleetHub(hub1)
                    .isVirtual(true)
                    .currentBookingId("BK-20240130-001")
                    .currentDriverId("DRV-001")
                    .build();

            // Vehicle 3 - Virtual, Charging
            Vehicle vehicle3 = Vehicle.builder()
                    .model(model2)
                    .plateNumber("51C-11111")
                    .vin("VIN1234567890003")
                    .color("Red")
                    .manufactureYear(2024)
                    .status(VehicleStatus.CHARGING)
                    .odometerKm(567.2)
                    .fleetHub(hub2)
                    .isVirtual(true)
                    .build();

            // Vehicle 4 - Virtual, Maintenance
            Vehicle vehicle4 = Vehicle.builder()
                    .model(model2)
                    .plateNumber("51D-22222")
                    .vin("VIN1234567890004")
                    .color("Blue")
                    .manufactureYear(2023)
                    .status(VehicleStatus.MAINTENANCE)
                    .odometerKm(15432.1)
                    .fleetHub(hub2)
                    .isVirtual(true)
                    .build();

            // Vehicle 5 - Virtual, Available
            Vehicle vehicle5 = Vehicle.builder()
                    .model(model3)
                    .plateNumber("59E-33333")
                    .vin("VIN1234567890005")
                    .color("Gray")
                    .manufactureYear(2024)
                    .status(VehicleStatus.AVAILABLE)
                    .odometerKm(234.7)
                    .fleetHub(hub3)
                    .isVirtual(true)
                    .build();

            vehicle1 = vehicleRepository.save(vehicle1);
            vehicle2 = vehicleRepository.save(vehicle2);
            vehicle3 = vehicleRepository.save(vehicle3);
            vehicle4 = vehicleRepository.save(vehicle4);
            vehicle5 = vehicleRepository.save(vehicle5);
            log.info("Created {} vehicles", vehicleRepository.count());

            // Create Vehicle States
            log.info("Creating vehicle states...");

            VehicleState state1 = VehicleState.builder()
                    .vehicle(vehicle1)
                    .latitude(10.762622)
                    .longitude(106.660172)
                    .batteryLevel(85)
                    .isCharging(false)
                    .speedKmh(0.0)
                    .odometerKm(1250.5)
                    .lastUpdatedAt(LocalDateTime.now())
                    .dataSource(DataSource.SYSTEM)
                    .messageSequence(0L)
                    .build();

            VehicleState state2 = VehicleState.builder()
                    .vehicle(vehicle2)
                    .latitude(10.776889)
                    .longitude(106.700806)
                    .batteryLevel(65)
                    .isCharging(false)
                    .speedKmh(45.5)
                    .odometerKm(3456.8)
                    .lastUpdatedAt(LocalDateTime.now())
                    .dataSource(DataSource.VIRTUAL_CAR)
                    .messageSequence(125L)
                    .build();

            VehicleState state3 = VehicleState.builder()
                    .vehicle(vehicle3)
                    .latitude(10.729509)
                    .longitude(106.719971)
                    .batteryLevel(35)
                    .isCharging(true)
                    .speedKmh(0.0)
                    .odometerKm(567.2)
                    .lastUpdatedAt(LocalDateTime.now())
                    .dataSource(DataSource.SYSTEM)
                    .messageSequence(0L)
                    .build();

            VehicleState state4 = VehicleState.builder()
                    .vehicle(vehicle4)
                    .latitude(10.738076)
                    .longitude(106.678459)
                    .batteryLevel(0)
                    .isCharging(false)
                    .speedKmh(0.0)
                    .odometerKm(15432.1)
                    .lastUpdatedAt(LocalDateTime.now().minusDays(3))
                    .dataSource(DataSource.MANUAL)
                    .messageSequence(0L)
                    .build();

            VehicleState state5 = VehicleState.builder()
                    .vehicle(vehicle5)
                    .latitude(10.853163)
                    .longitude(106.62816)
                    .batteryLevel(95)
                    .isCharging(false)
                    .speedKmh(0.0)
                    .odometerKm(234.7)
                    .lastUpdatedAt(LocalDateTime.now())
                    .dataSource(DataSource.SYSTEM)
                    .messageSequence(0L)
                    .build();

            vehicleStateRepository.save(state1);
            vehicleStateRepository.save(state2);
            vehicleStateRepository.save(state3);
            vehicleStateRepository.save(state4);
            vehicleStateRepository.save(state5);
            log.info("Created {} vehicle states", vehicleStateRepository.count());

            log.info("=== Data Initialization Completed Successfully ===");
            log.info("Summary:");
            log.info("  - Fleet Hubs: {}", fleetHubRepository.count());
            log.info("  - Vehicle Models: {}", vehicleModelRepository.count());
            log.info("  - Vehicles: {}", vehicleRepository.count());
            log.info("  - Vehicle States: {}", vehicleStateRepository.count());
        };
    }
}
