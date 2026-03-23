package com.parking.controller;

import com.parking.dto.request.CheckInRequest;
import com.parking.dto.request.CheckOutRequest;
import com.parking.dto.response.ActiveSessionResponse;
import com.parking.dto.response.CheckInResponse;
import com.parking.dto.response.CheckOutResponse;
import com.parking.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking/lots/{lotId}")
@RequiredArgsConstructor
@Tag(name = "Parking", description = "Vehicle check-in/check-out and session management")
public class ParkingController {

    private final ParkingService parkingService;

    @Operation(summary = "Check in a vehicle",
            description = "Assigns an available compatible slot to the vehicle and creates a parking ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle checked in successfully"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found"),
            @ApiResponse(responseCode = "409", description = "Vehicle already parked or no compatible slot available")
    })
    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponse> checkIn(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingService.checkIn(lotId, request));
    }

    @Operation(summary = "Check out a vehicle",
            description = "Ends the parking session, calculates the fee, and frees the slot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle checked out with fee calculated"),
            @ApiResponse(responseCode = "404", description = "No active session found for vehicle")
    })
    @PostMapping("/check-out")
    public ResponseEntity<CheckOutResponse> checkOut(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Valid @RequestBody CheckOutRequest request) {
        return ResponseEntity.ok(parkingService.checkOut(lotId, request));
    }

    @Operation(summary = "View active parking sessions",
            description = "Returns all currently active parking sessions for the specified lot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active sessions"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found")
    })
    @GetMapping("/sessions")
    public ResponseEntity<List<ActiveSessionResponse>> getActiveSessions(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId) {
        return ResponseEntity.ok(parkingService.getActiveSessions(lotId));
    }
}
