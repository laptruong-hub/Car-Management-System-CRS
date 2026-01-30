package com.crs.carmanagement.enums;

/**
 * Data source enumeration for tracking where vehicle data originates
 */
public enum DataSource {
    VIRTUAL_CAR, // Data from virtual car simulator via RabbitMQ
    MANUAL, // Manually entered by admin/operator
    SYSTEM // System generated or calculated
}
