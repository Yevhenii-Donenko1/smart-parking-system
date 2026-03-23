package com.parking.dto.response;

import com.parking.model.enums.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Check-in receipt with assigned slot details")
public record CheckInResponse(
        @Schema(description = "Parking ticket ID", example = "1")
        Long ticketId,
        @Schema(description = "Vehicle license plate", example = "ABC-123")
        String licensePlate,
        @Schema(description = "Assigned floor number", example = "1")
        int floorNumber,
        @Schema(description = "Assigned slot number", example = "1")
        int slotNumber,
        @Schema(description = "Assigned slot type", example = "COMPACT")
        SlotType slotType,
        @Schema(description = "Entry timestamp")
        LocalDateTime entryTime
) {
}
