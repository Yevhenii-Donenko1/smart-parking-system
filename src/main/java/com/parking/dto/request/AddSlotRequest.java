package com.parking.dto.request;

import com.parking.model.enums.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to add a parking slot to a level")
public record AddSlotRequest(
        @Min(value = 1, message = "Slot number must be at least 1")
        @Schema(description = "Slot number within the level", example = "1")
        int slotNumber,

        @NotNull(message = "Slot type is required")
        @Schema(description = "Type of the parking slot", example = "COMPACT")
        SlotType slotType
) {
}
