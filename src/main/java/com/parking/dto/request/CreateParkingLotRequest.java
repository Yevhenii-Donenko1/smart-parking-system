package com.parking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new parking lot")
public record CreateParkingLotRequest(
        @NotBlank(message = "Parking lot name is required")
        @Schema(description = "Unique name for the parking lot", example = "Downtown Garage")
        String name
) {
}
