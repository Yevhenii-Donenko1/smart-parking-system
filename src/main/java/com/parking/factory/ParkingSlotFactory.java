package com.parking.factory;

import com.parking.model.entity.Level;
import com.parking.model.entity.ParkingSlot;
import com.parking.model.enums.SlotType;
import org.springframework.stereotype.Component;

@Component
public class ParkingSlotFactory {

    public ParkingSlot create(int slotNumber, SlotType slotType, Level level) {
        return ParkingSlot.builder()
                .slotNumber(slotNumber)
                .slotType(slotType)
                .level(level)
                .build();
    }
}
