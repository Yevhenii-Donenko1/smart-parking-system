package com.parking.dto.request;

import com.parking.model.enums.SlotStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to update a slot's status (AVAILABLE or MAINTENANCE only)")
public record UpdateSlotStatusRequest(
        @NotNull(message = "Status is required")
        @Schema(description = "New status for the slot", example = "MAINTENANCE")
        SlotStatus status
) {
}
