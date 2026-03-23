package com.parking.service;

import com.parking.dto.request.CheckInRequest;
import com.parking.dto.request.CheckOutRequest;
import com.parking.dto.response.CheckInResponse;
import com.parking.dto.response.CheckOutResponse;
import com.parking.exception.SlotNotAvailableException;
import com.parking.exception.VehicleAlreadyParkedException;
import com.parking.exception.VehicleNotParkedException;
import com.parking.factory.VehicleFactory;
import com.parking.model.entity.Level;
import com.parking.model.entity.ParkingLot;
import com.parking.model.entity.ParkingSlot;
import com.parking.model.entity.ParkingTicket;
import com.parking.model.entity.Vehicle;
import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import com.parking.model.enums.VehicleType;
import com.parking.repository.ParkingLotRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.ParkingTicketRepository;
import com.parking.strategy.assignment.SlotAssignmentStrategy;
import com.parking.strategy.fee.FeeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock private ParkingLotRepository parkingLotRepository;
    @Mock private VehicleFactory vehicleFactory;
    @Mock private ParkingSlotRepository parkingSlotRepository;
    @Mock private ParkingTicketRepository parkingTicketRepository;
    @Mock private SlotAssignmentStrategy slotAssignmentStrategy;
    @Mock private FeeStrategy feeStrategy;

    @InjectMocks
    private ParkingService parkingService;

    private ParkingLot lot;
    private Level level;
    private ParkingSlot slot;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        lot = ParkingLot.builder().id(1L).name("Test Lot").build();
        level = Level.builder().id(1L).floorNumber(1).parkingLot(lot).build();
        slot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.AVAILABLE).level(level).build();
        vehicle = Vehicle.builder().id(1L).licensePlate("ABC-123").vehicleType(VehicleType.CAR).build();
    }

    @Test
    void checkIn_success() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        when(parkingTicketRepository.findByVehicleLicensePlateAndExitTimeIsNull("ABC-123")).thenReturn(Optional.empty());
        when(vehicleFactory.getOrCreate("ABC-123", VehicleType.CAR)).thenReturn(vehicle);
        when(slotAssignmentStrategy.findSlot(1L, VehicleType.CAR)).thenReturn(Optional.of(slot));
        when(parkingSlotRepository.save(any())).thenReturn(slot);
        when(parkingTicketRepository.save(any())).thenAnswer(inv -> {
            ParkingTicket t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        CheckInResponse response = parkingService.checkIn(1L, new CheckInRequest("ABC-123", VehicleType.CAR));

        assertNotNull(response);
        assertEquals("ABC-123", response.licensePlate());
        assertEquals(1, response.floorNumber());
        assertEquals(1, response.slotNumber());
        verify(parkingSlotRepository).save(slot);
        assertEquals(SlotStatus.OCCUPIED, slot.getStatus());
    }

    @Test
    void checkIn_vehicleAlreadyParked() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        ParkingTicket activeTicket = ParkingTicket.builder().id(1L).build();
        when(parkingTicketRepository.findByVehicleLicensePlateAndExitTimeIsNull("ABC-123"))
                .thenReturn(Optional.of(activeTicket));

        assertThrows(VehicleAlreadyParkedException.class,
                () -> parkingService.checkIn(1L, new CheckInRequest("ABC-123", VehicleType.CAR)));
    }

    @Test
    void checkIn_noSlotsAvailable() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        when(parkingTicketRepository.findByVehicleLicensePlateAndExitTimeIsNull("ABC-123")).thenReturn(Optional.empty());
        when(vehicleFactory.getOrCreate("ABC-123", VehicleType.CAR)).thenReturn(vehicle);
        when(slotAssignmentStrategy.findSlot(1L, VehicleType.CAR)).thenReturn(Optional.empty());

        assertThrows(SlotNotAvailableException.class,
                () -> parkingService.checkIn(1L, new CheckInRequest("ABC-123", VehicleType.CAR)));
    }

    @Test
    void checkOut_success() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        ParkingTicket ticket = ParkingTicket.builder()
                .id(1L)
                .vehicle(vehicle)
                .parkingSlot(slot)
                .entryTime(LocalDateTime.now().minusHours(2))
                .build();
        slot.setStatus(SlotStatus.OCCUPIED);

        when(parkingTicketRepository.findByVehicleLicensePlateAndExitTimeIsNull("ABC-123"))
                .thenReturn(Optional.of(ticket));
        when(feeStrategy.calculateFee(eq(VehicleType.CAR), any(), any())).thenReturn(new BigDecimal("4.00"));
        when(parkingTicketRepository.save(any())).thenReturn(ticket);
        when(parkingSlotRepository.save(any())).thenReturn(slot);

        CheckOutResponse response = parkingService.checkOut(1L, new CheckOutRequest("ABC-123"));

        assertNotNull(response);
        assertEquals(new BigDecimal("4.00"), response.fee());
        assertEquals(SlotStatus.AVAILABLE, slot.getStatus());
    }

    @Test
    void checkOut_vehicleNotParked() {
        when(parkingLotRepository.existsById(1L)).thenReturn(true);
        when(parkingTicketRepository.findByVehicleLicensePlateAndExitTimeIsNull("XYZ-999"))
                .thenReturn(Optional.empty());

        assertThrows(VehicleNotParkedException.class,
                () -> parkingService.checkOut(1L, new CheckOutRequest("XYZ-999")));
    }
}
