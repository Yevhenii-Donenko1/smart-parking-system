package com.parking.dto.request;

import com.parking.model.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to check in a vehicle")
public record CheckInRequest(
        @NotBlank(message = "License plate is required")
        @Schema(description = "Vehicle license plate", example = "ABC-123")
        String licensePlate,

        @NotNull(message = "Vehicle type is required")
        @Schema(description = "Type of the vehicle", example = "CAR")
        VehicleType vehicleType
) {
}
