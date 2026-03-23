package com.parking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to check out a vehicle")
public record CheckOutRequest(
        @NotBlank(message = "License plate is required")
        @Schema(description = "Vehicle license plate", example = "ABC-123")
        String licensePlate
) {
}
