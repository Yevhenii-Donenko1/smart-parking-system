package com.parking.strategy.assignment;

import com.parking.model.entity.ParkingSlot;
import com.parking.model.enums.VehicleType;

import java.util.Optional;

public interface SlotAssignmentStrategy {
    Optional<ParkingSlot> findSlot(Long lotId, VehicleType vehicleType);
}
