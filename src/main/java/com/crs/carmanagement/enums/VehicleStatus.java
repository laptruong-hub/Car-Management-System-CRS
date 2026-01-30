package com.crs.carmanagement.enums;

/**
 * Vehicle status enumeration
 * Represents the current operational status of a vehicle
 */
public enum VehicleStatus {
    AVAILABLE, // Vehicle is available for rent
    IN_USE, // Vehicle is currently being used/rented
    MAINTENANCE, // Vehicle is under maintenance
    DAMAGED, // Vehicle is damaged and unavailable
    CHARGING // Vehicle is currently charging
}
