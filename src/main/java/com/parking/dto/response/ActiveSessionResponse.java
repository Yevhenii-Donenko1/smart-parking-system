package com.parking.dto.response;

import com.parking.model.enums.SlotType;
import com.parking.model.enums.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Active parking session details")
public record ActiveSessionResponse(
        @Schema(description = "Parking ticket ID", example = "1")
        Long ticketId,
        @Schema(description = "Vehicle license plate", example = "ABC-123")
        String licensePlate,
        @Schema(description = "Vehicle type", example = "CAR")
        VehicleType vehicleType,
        @Schema(description = "Floor number", example = "1")
        int floorNumber,
        @Schema(description = "Slot number", example = "1")
        int slotNumber,
        @Schema(description = "Slot type", example = "COMPACT")
        SlotType slotType,
        @Schema(description = "Entry timestamp")
        LocalDateTime entryTime
) {
}
