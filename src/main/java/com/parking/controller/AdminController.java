package com.parking.controller;

import com.parking.dto.request.*;
import com.parking.dto.response.*;
import com.parking.service.AdminService;
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
@RequestMapping("/api/v1/admin/lots")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Parking lot administration endpoints")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Create a parking lot", description = "Creates a new parking lot with a unique name")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parking lot created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ParkingLotResponse> createParkingLot(@Valid @RequestBody CreateParkingLotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createParkingLot(request));
    }

    @Operation(summary = "List all parking lots", description = "Returns all parking lots with their levels and slots")
    @GetMapping
    public ResponseEntity<List<ParkingLotResponse>> getAllParkingLots() {
        return ResponseEntity.ok(adminService.getAllParkingLots());
    }

    @Operation(summary = "Get a parking lot by ID", description = "Returns a single parking lot with its levels and slots")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parking lot found"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found")
    })
    @GetMapping("/{lotId}")
    public ResponseEntity<ParkingLotResponse> getParkingLot(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId) {
        return ResponseEntity.ok(adminService.getParkingLot(lotId));
    }

    @Operation(summary = "Delete a parking lot", description = "Deletes a parking lot. Fails if there are active parking sessions")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Parking lot deleted"),
            @ApiResponse(responseCode = "400", description = "Cannot delete lot with active sessions"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found")
    })
    @DeleteMapping("/{lotId}")
    public ResponseEntity<Void> deleteParkingLot(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId) {
        adminService.deleteParkingLot(lotId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a level to a parking lot", description = "Adds a new floor level to the specified parking lot")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Level added successfully"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found")
    })
    @PostMapping("/{lotId}/levels")
    public ResponseEntity<LevelResponse> addLevel(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Valid @RequestBody AddLevelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addLevel(lotId, request));
    }

    @Operation(summary = "Remove a level", description = "Removes a level from a parking lot. Fails if any slots are occupied")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Level removed"),
            @ApiResponse(responseCode = "400", description = "Cannot remove level with occupied slots"),
            @ApiResponse(responseCode = "404", description = "Level or parking lot not found")
    })
    @DeleteMapping("/{lotId}/levels/{levelId}")
    public ResponseEntity<Void> removeLevel(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Parameter(description = "Level ID") @PathVariable Long levelId) {
        adminService.removeLevel(lotId, levelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add a slot to a level", description = "Adds a new parking slot to the specified level")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Slot added successfully"),
            @ApiResponse(responseCode = "404", description = "Level or parking lot not found")
    })
    @PostMapping("/{lotId}/levels/{levelId}/slots")
    public ResponseEntity<SlotResponse> addSlot(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Parameter(description = "Level ID") @PathVariable Long levelId,
            @Valid @RequestBody AddSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addSlot(lotId, levelId, request));
    }

    @Operation(summary = "Remove a slot", description = "Removes a parking slot. Fails if the slot is currently occupied")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Slot removed"),
            @ApiResponse(responseCode = "400", description = "Cannot remove occupied slot"),
            @ApiResponse(responseCode = "404", description = "Slot, level, or parking lot not found")
    })
    @DeleteMapping("/{lotId}/levels/{levelId}/slots/{slotId}")
    public ResponseEntity<Void> removeSlot(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Parameter(description = "Level ID") @PathVariable Long levelId,
            @Parameter(description = "Slot ID") @PathVariable Long slotId) {
        adminService.removeSlot(lotId, levelId, slotId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update slot status", description = "Toggle slot between AVAILABLE and MAINTENANCE. Cannot set to OCCUPIED manually")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Slot status updated"),
            @ApiResponse(responseCode = "400", description = "Invalid status change"),
            @ApiResponse(responseCode = "404", description = "Slot not found")
    })
    @PatchMapping("/{lotId}/levels/{levelId}/slots/{slotId}/status")
    public ResponseEntity<SlotResponse> updateSlotStatus(
            @Parameter(description = "Parking lot ID") @PathVariable Long lotId,
            @Parameter(description = "Level ID") @PathVariable Long levelId,
            @Parameter(description = "Slot ID") @PathVariable Long slotId,
            @Valid @RequestBody UpdateSlotStatusRequest request) {
        return ResponseEntity.ok(adminService.updateSlotStatus(lotId, levelId, slotId, request));
    }
}
