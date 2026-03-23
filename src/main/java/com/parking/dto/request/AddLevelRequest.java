package com.parking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "Request to add a level to a parking lot")
public record AddLevelRequest(
        @Min(value = 1, message = "Floor number must be at least 1")
        @Schema(description = "Floor number for the level", example = "1")
        int floorNumber
) {
}
