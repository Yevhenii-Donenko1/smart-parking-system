package com.parking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Check-out summary with duration and fee")
public record CheckOutResponse(
        @Schema(description = "Parking ticket ID", example = "1")
        Long ticketId,
        @Schema(description = "Vehicle license plate", example = "ABC-123")
        String licensePlate,
        @Schema(description = "Entry timestamp")
        LocalDateTime entryTime,
        @Schema(description = "Exit timestamp")
        LocalDateTime exitTime,
        @Schema(description = "Duration in hours (rounded up)", example = "3")
        long durationHours,
        @Schema(description = "Calculated parking fee", example = "6.00")
        BigDecimal fee
) {
}
