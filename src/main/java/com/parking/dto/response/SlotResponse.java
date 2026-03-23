package com.parking.dto.response;

import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parking slot details")
public record SlotResponse(
        @Schema(description = "Slot ID", example = "1")
        Long id,
        @Schema(description = "Slot number within the level", example = "1")
        int slotNumber,
        @Schema(description = "Type of slot", example = "COMPACT")
        SlotType slotType,
        @Schema(description = "Current status of the slot", example = "AVAILABLE")
        SlotStatus status
) {
}
