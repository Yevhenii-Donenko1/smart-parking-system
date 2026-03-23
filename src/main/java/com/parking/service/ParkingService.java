package com.parking.service;

import com.parking.dto.request.CheckInRequest;
import com.parking.dto.request.CheckOutRequest;
import com.parking.dto.response.ActiveSessionResponse;
import com.parking.dto.response.CheckInResponse;
import com.parking.dto.response.CheckOutResponse;
import com.parking.exception.InvalidOperationException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.exception.SlotNotAvailableException;
import com.parking.exception.VehicleAlreadyParkedException;
import com.parking.exception.VehicleNotParkedException;
import com.parking.factory.VehicleFactory;
import com.parking.model.entity.ParkingSlot;
import com.parking.model.entity.ParkingTicket;
import com.parking.model.entity.Vehicle;
import com.parking.model.enums.SlotStatus;
import com.parking.repository.ParkingLotRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.ParkingTicketRepository;
import com.parking.strategy.assignment.SlotAssignmentStrategy;
import com.parking.strategy.fee.FeeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingService {

    private final ParkingLotRepository parkingLotRepository;
    private final VehicleFactory vehicleFactory;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingTicketRepository parkingTicketRepository;
    private final SlotAssignmentStrategy slotAssignmentStrategy;
    private final FeeStrategy feeStrategy;

    public CheckInResponse checkIn(Long lotId, CheckInRequest request) {
        validateLotExists(lotId);

        parkingTicketRepository.findByVehicleLicensePlateAndExitTimeIsNull(request.licensePlate())
                .ifPresent(t -> {
                    throw new VehicleAlreadyParkedException(
                            "Vehicle with license plate " + request.licensePlate() + " is already parked");
                });

        Vehicle vehicle = vehicleFactory.getOrCreate(request.licensePlate(), request.vehicleType());

        if (vehicle.getVehicleType() != request.vehicleType()) {
            throw new InvalidOperationException(
                    "Vehicle " + request.licensePlate() + " is registered as " + vehicle.getVehicleType()
                            + ", cannot check in as " + request.vehicleType());
        }

        ParkingSlot slot = slotAssignmentStrategy.findSlot(lotId, vehicle.getVehicleType())
                .orElseThrow(() -> new SlotNotAvailableException(
                        "No compatible slot available for vehicle type: " + vehicle.getVehicleType()));

        slot.setStatus(SlotStatus.OCCUPIED);
        parkingSlotRepository.save(slot);

        LocalDateTime now = LocalDateTime.now();
        ParkingTicket ticket = ParkingTicket.builder()
                .vehicle(vehicle)
                .parkingSlot(slot)
                .entryTime(now)
                .build();
        ticket = parkingTicketRepository.save(ticket);

        return new CheckInResponse(
                ticket.getId(),
                vehicle.getLicensePlate(),
                slot.getLevel().getFloorNumber(),
                slot.getSlotNumber(),
                slot.getSlotType(),
                ticket.getEntryTime()
        );
    }

    public CheckOutResponse checkOut(Long lotId, CheckOutRequest request) {
        validateLotExists(lotId);

        ParkingTicket ticket = parkingTicketRepository
                .findByVehicleLicensePlateAndExitTimeIsNull(request.licensePlate())
                .orElseThrow(() -> new VehicleNotParkedException(
                        "No active parking session found for vehicle: " + request.licensePlate()));

        if (!ticket.getParkingSlot().getLevel().getParkingLot().getId().equals(lotId)) {
            throw new VehicleNotParkedException(
                    "No active parking session found for vehicle in this lot: " + request.licensePlate());
        }

        LocalDateTime exitTime = LocalDateTime.now();
        BigDecimal fee = feeStrategy.calculateFee(
                ticket.getVehicle().getVehicleType(),
                ticket.getEntryTime(),
                exitTime);

        ticket.setExitTime(exitTime);
        ticket.setFee(fee);
        parkingTicketRepository.save(ticket);

        ParkingSlot slot = ticket.getParkingSlot();
        slot.setStatus(SlotStatus.AVAILABLE);
        parkingSlotRepository.save(slot);

        long durationHours = calculateDurationHours(ticket.getEntryTime(), exitTime);

        return new CheckOutResponse(
                ticket.getId(),
                ticket.getVehicle().getLicensePlate(),
                ticket.getEntryTime(),
                exitTime,
                durationHours,
                fee
        );
    }

    @Transactional(readOnly = true)
    public List<ActiveSessionResponse> getActiveSessions(Long lotId) {
        validateLotExists(lotId);

        return parkingTicketRepository.findByParkingSlotLevelParkingLotIdAndExitTimeIsNull(lotId)
                .stream()
                .map(ticket -> new ActiveSessionResponse(
                        ticket.getId(),
                        ticket.getVehicle().getLicensePlate(),
                        ticket.getVehicle().getVehicleType(),
                        ticket.getParkingSlot().getLevel().getFloorNumber(),
                        ticket.getParkingSlot().getSlotNumber(),
                        ticket.getParkingSlot().getSlotType(),
                        ticket.getEntryTime()
                ))
                .toList();
    }

    private void validateLotExists(Long lotId) {
        if (!parkingLotRepository.existsById(lotId)) {
            throw new ResourceNotFoundException("Parking lot not found with id: " + lotId);
        }
    }

    private long calculateDurationHours(LocalDateTime entry, LocalDateTime exit) {
        long hours = (long) Math.ceil(Duration.between(entry, exit).toMinutes() / 60.0);
        return Math.max(hours, 1);
    }
}
