package com.parking.strategy.assignment;

import com.parking.model.entity.Level;
import com.parking.model.entity.ParkingLot;
import com.parking.model.entity.ParkingSlot;
import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import com.parking.model.enums.VehicleType;
import com.parking.repository.ParkingSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NearestSlotStrategyTest {

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    @InjectMocks
    private NearestSlotStrategy strategy;

    private ParkingLot lot;
    private Level level1;

    @BeforeEach
    void setUp() {
        lot = ParkingLot.builder().id(1L).name("Test Lot").build();
        level1 = Level.builder().id(1L).floorNumber(1).parkingLot(lot).build();
    }

    @Test
    void picksFirstAvailableSlot() {
        ParkingSlot slot1 = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.AVAILABLE).level(level1).build();
        ParkingSlot slot2 = ParkingSlot.builder().id(2L).slotNumber(2).slotType(SlotType.COMPACT).status(SlotStatus.AVAILABLE).level(level1).build();

        when(parkingSlotRepository.findAvailableSlotsByLotAndCompatibleTypes(eq(1L), any(), eq(SlotStatus.AVAILABLE)))
                .thenReturn(List.of(slot1, slot2));

        Optional<ParkingSlot> result = strategy.findSlot(1L, VehicleType.CAR);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void returnsEmptyWhenNoSlotsAvailable() {
        when(parkingSlotRepository.findAvailableSlotsByLotAndCompatibleTypes(eq(1L), any(), eq(SlotStatus.AVAILABLE)))
                .thenReturn(Collections.emptyList());

        Optional<ParkingSlot> result = strategy.findSlot(1L, VehicleType.CAR);

        assertTrue(result.isEmpty());
    }

    @Test
    void motorcycleCanUseCompactSlot() {
        ParkingSlot compactSlot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.AVAILABLE).level(level1).build();

        when(parkingSlotRepository.findAvailableSlotsByLotAndCompatibleTypes(eq(1L), eq(VehicleType.MOTORCYCLE.getCompatibleSlots()), eq(SlotStatus.AVAILABLE)))
                .thenReturn(List.of(compactSlot));

        Optional<ParkingSlot> result = strategy.findSlot(1L, VehicleType.MOTORCYCLE);

        assertTrue(result.isPresent());
        assertEquals(SlotType.COMPACT, result.get().getSlotType());
    }
}
