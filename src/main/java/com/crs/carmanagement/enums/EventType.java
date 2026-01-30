package com.crs.carmanagement.enums;

/**
 * Event type enumeration for vehicle event logging (Option B)
 * Used to categorize different types of events in VehicleEventLog
 */
public enum EventType {
    // Vehicle lifecycle events
    VEHICLE_CREATED,
    VEHICLE_UPDATED,
    VEHICLE_DELETED,

    // State change events
    STATE_UPDATED,
    STATUS_CHANGED,
    LOCATION_UPDATED,
    BATTERY_UPDATED,

    // Operational events
    BOOKING_STARTED,
    BOOKING_COMPLETED,
    DRIVER_ASSIGNED,
    DRIVER_UNASSIGNED,

    // Maintenance events
    MAINTENANCE_STARTED,
    MAINTENANCE_COMPLETED,
    DAMAGE_REPORTED,
    DAMAGE_REPAIRED,

    // Charging events
    CHARGING_STARTED,
    CHARGING_COMPLETED,

    // Alert events
    LOW_BATTERY_ALERT,
    GEOFENCE_VIOLATION,
    ANOMALY_DETECTED
}
