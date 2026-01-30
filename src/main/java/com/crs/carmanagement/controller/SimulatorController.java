package com.crs.carmanagement.controller;

import com.crs.carmanagement.config.SimulatorConfig;
import com.crs.carmanagement.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for controlling the Virtual Car Simulator
 */
@RestController
@RequestMapping("/api/v1/simulator")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Simulator Control", description = "Control virtual car simulator settings")
public class SimulatorController {

    private final SimulatorConfig config;

    /**
     * Get current simulator configuration
     */
    @GetMapping("/config")
    @Operation(summary = "Get simulator config", description = "Get current simulator configuration and status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("enabled", config.isEnabled());
        configMap.put("updateIntervalMs", config.getUpdateIntervalMs());
        configMap.put("statusChangeIntervalMs", config.getStatusChangeIntervalMs());
        configMap.put("vehicleIds", config.getVehicleIds());
        configMap.put("vehicleCount", config.getVehicleIds().isEmpty() ? "ALL" : config.getVehicleIds().size());

        return ResponseEntity.ok(ApiResponse.success(configMap, "Simulator config retrieved"));
    }

    /**
     * Enable simulator
     */
    @PostMapping("/enable")
    @Operation(summary = "Enable simulator", description = "Turn on virtual car simulation")
    public ResponseEntity<ApiResponse<String>> enable() {
        config.setEnabled(true);
        log.info("Simulator ENABLED via API");
        return ResponseEntity.ok(ApiResponse.success("Simulator enabled successfully"));
    }

    /**
     * Disable simulator
     */
    @PostMapping("/disable")
    @Operation(summary = "Disable simulator", description = "Turn off virtual car simulation")
    public ResponseEntity<ApiResponse<String>> disable() {
        config.setEnabled(false);
        log.info("Simulator DISABLED via API");
        return ResponseEntity.ok(ApiResponse.success("Simulator disabled successfully"));
    }

    /**
     * Set specific vehicles to simulate
     */
    @PutMapping("/vehicles")
    @Operation(summary = "Set vehicle whitelist", description = "Set specific vehicle IDs to simulate. Empty list = simulate all vehicles")
    public ResponseEntity<ApiResponse<Map<String, Object>>> setVehicles(
            @RequestBody List<Long> vehicleIds) {

        log.info("Setting simulator vehicle whitelist: {}", vehicleIds);
        config.setVehicleIds(vehicleIds);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleIds", config.getVehicleIds());
        result.put("mode", config.getVehicleIds().isEmpty() ? "ALL_VEHICLES" : "WHITELIST");

        return ResponseEntity.ok(ApiResponse.success(result,
                "Vehicle whitelist updated. " +
                        (vehicleIds.isEmpty() ? "Simulating ALL vehicles"
                                : "Simulating " + vehicleIds.size() + " vehicles")));
    }

    /**
     * Add a vehicle to simulation whitelist
     */
    @PostMapping("/vehicles/{vehicleId}")
    @Operation(summary = "Add vehicle to simulation", description = "Add a specific vehicle to simulation whitelist")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addVehicle(@PathVariable Long vehicleId) {
        if (!config.getVehicleIds().contains(vehicleId)) {
            config.getVehicleIds().add(vehicleId);
            log.info("Added vehicle {} to simulator whitelist", vehicleId);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleIds", config.getVehicleIds());
        result.put("added", vehicleId);

        return ResponseEntity.ok(ApiResponse.success(result,
                "Vehicle " + vehicleId + " added to simulation"));
    }

    /**
     * Remove a vehicle from simulation whitelist
     */
    @DeleteMapping("/vehicles/{vehicleId}")
    @Operation(summary = "Remove vehicle from simulation", description = "Remove a specific vehicle from simulation whitelist")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeVehicle(@PathVariable Long vehicleId) {
        config.getVehicleIds().remove(vehicleId);
        log.info("Removed vehicle {} from simulator whitelist", vehicleId);

        Map<String, Object> result = new HashMap<>();
        result.put("vehicleIds", config.getVehicleIds());
        result.put("removed", vehicleId);

        return ResponseEntity.ok(ApiResponse.success(result,
                "Vehicle " + vehicleId + " removed from simulation"));
    }
}
