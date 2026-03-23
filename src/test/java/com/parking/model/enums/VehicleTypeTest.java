package com.parking.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeTest {

    @Test
    void motorcycle_canFitInMotorcycleSlot() {
        assertTrue(VehicleType.MOTORCYCLE.canFitIn(SlotType.MOTORCYCLE));
    }

    @Test
    void motorcycle_canFitInCompactSlot() {
        assertTrue(VehicleType.MOTORCYCLE.canFitIn(SlotType.COMPACT));
    }

    @Test
    void motorcycle_canFitInLargeSlot() {
        assertTrue(VehicleType.MOTORCYCLE.canFitIn(SlotType.LARGE));
    }

    @Test
    void motorcycle_cannotFitInHandicappedSlot() {
        assertFalse(VehicleType.MOTORCYCLE.canFitIn(SlotType.HANDICAPPED));
    }

    @Test
    void car_canFitInCompactSlot() {
        assertTrue(VehicleType.CAR.canFitIn(SlotType.COMPACT));
    }

    @Test
    void car_canFitInLargeSlot() {
        assertTrue(VehicleType.CAR.canFitIn(SlotType.LARGE));
    }

    @Test
    void car_canFitInHandicappedSlot() {
        assertTrue(VehicleType.CAR.canFitIn(SlotType.HANDICAPPED));
    }

    @Test
    void car_cannotFitInMotorcycleSlot() {
        assertFalse(VehicleType.CAR.canFitIn(SlotType.MOTORCYCLE));
    }

    @Test
    void truck_canFitInLargeSlot() {
        assertTrue(VehicleType.TRUCK.canFitIn(SlotType.LARGE));
    }

    @Test
    void truck_cannotFitInCompactSlot() {
        assertFalse(VehicleType.TRUCK.canFitIn(SlotType.COMPACT));
    }

    @Test
    void truck_cannotFitInMotorcycleSlot() {
        assertFalse(VehicleType.TRUCK.canFitIn(SlotType.MOTORCYCLE));
    }

    @Test
    void truck_cannotFitInHandicappedSlot() {
        assertFalse(VehicleType.TRUCK.canFitIn(SlotType.HANDICAPPED));
    }
}
