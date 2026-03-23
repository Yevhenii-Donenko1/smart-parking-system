package com.parking.model.enums;

import java.util.Set;

public enum VehicleType {
    MOTORCYCLE(Set.of(SlotType.MOTORCYCLE, SlotType.COMPACT, SlotType.LARGE)),
    CAR(Set.of(SlotType.COMPACT, SlotType.LARGE, SlotType.HANDICAPPED)),
    TRUCK(Set.of(SlotType.LARGE));

    private final Set<SlotType> compatibleSlots;

    VehicleType(Set<SlotType> compatibleSlots) {
        this.compatibleSlots = compatibleSlots;
    }

    public Set<SlotType> getCompatibleSlots() {
        return compatibleSlots;
    }

    public boolean canFitIn(SlotType slotType) {
        return compatibleSlots.contains(slotType);
    }
}
