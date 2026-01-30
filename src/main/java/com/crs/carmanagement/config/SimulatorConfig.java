package com.crs.carmanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for Virtual Car Simulator
 */
@Configuration
@ConfigurationProperties(prefix = "simulator")
@Data
public class SimulatorConfig {

    /**
     * Enable/disable the entire simulator
     */
    private boolean enabled = true;

    /**
     * Update interval in milliseconds (default: 5000ms = 5 seconds)
     */
    private long updateIntervalMs = 5000;

    /**
     * Random status change interval in milliseconds (default: 30000ms = 30 seconds)
     */
    private long statusChangeIntervalMs = 30000;

    /**
     * List of vehicle IDs to simulate
     * If empty, all virtual vehicles will be simulated
     * If specified, only these vehicles will be simulated
     */
    private List<Long> vehicleIds = new ArrayList<>();

    /**
     * Check if a specific vehicle should be simulated
     */
    public boolean shouldSimulate(Long vehicleId) {
        if (!enabled) {
            return false;
        }

        // If no specific IDs configured, simulate all
        if (vehicleIds == null || vehicleIds.isEmpty()) {
            return true;
        }

        // Only simulate if in the whitelist
        return vehicleIds.contains(vehicleId);
    }
}
