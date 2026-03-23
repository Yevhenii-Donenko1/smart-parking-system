package com.parking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Level details with parking slots")
public record LevelResponse(
        @Schema(description = "Level ID", example = "1")
        Long id,
        @Schema(description = "Floor number", example = "1")
        int floorNumber,
        @Schema(description = "Parking slots on this level")
        List<SlotResponse> slots
) {
}
