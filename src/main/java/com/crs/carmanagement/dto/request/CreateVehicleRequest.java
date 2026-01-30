package com.crs.carmanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new vehicle
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequest {

    @NotNull(message = "Model ID is required")
    private Long modelId;

    @NotBlank(message = "Plate number is required")
    @Pattern(regexp = "^[0-9]{2}[A-Z]-[0-9]{4,5}$", message = "Plate number must follow format: 29A-12345")
    private String plateNumber;

    @NotBlank(message = "VIN is required")
    @Size(min = 17, max = 17, message = "VIN must be exactly 17 characters")
    private String vin;

    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @NotNull(message = "Manufacture year is required")
    @Min(value = 2020, message = "Manufacture year must be at least 2020")
    @Max(value = 2030, message = "Manufacture year must not exceed 2030")
    private Integer manufactureYear;

    @NotNull(message = "Fleet hub ID is required")
    private Long fleetHubId;

    @Builder.Default
    private Boolean isVirtual = true;

    @DecimalMin(value = "0.0", message = "Odometer cannot be negative")
    @Builder.Default
    private Double odometerKm = 0.0;
}
