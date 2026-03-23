package com.parking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Parking lot details with levels and slots")
public record ParkingLotResponse(
        @Schema(description = "Parking lot ID", example = "1")
        Long id,
        @Schema(description = "Parking lot name", example = "Downtown Garage")
        String name,
        @Schema(description = "Levels in the parking lot")
        List<LevelResponse> levels
) {
}
