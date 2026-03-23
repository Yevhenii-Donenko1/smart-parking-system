package com.parking.strategy.assignment;

import com.parking.model.entity.ParkingSlot;
import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.VehicleType;
import com.parking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NearestSlotStrategy implements SlotAssignmentStrategy {

    private final ParkingSlotRepository parkingSlotRepository;

    @Override
    public Optional<ParkingSlot> findSlot(Long lotId, VehicleType vehicleType) {
        List<ParkingSlot> availableSlots = parkingSlotRepository
                .findAvailableSlotsByLotAndCompatibleTypes(
                        lotId,
                        vehicleType.getCompatibleSlots(),
                        SlotStatus.AVAILABLE);
        return availableSlots.stream().findFirst();
    }
}
