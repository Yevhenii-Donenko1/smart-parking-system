package com.parking.service;

import com.parking.dto.request.AddLevelRequest;
import com.parking.dto.request.AddSlotRequest;
import com.parking.dto.request.CreateParkingLotRequest;
import com.parking.dto.request.UpdateSlotStatusRequest;
import com.parking.dto.response.LevelResponse;
import com.parking.dto.response.ParkingLotResponse;
import com.parking.dto.response.SlotResponse;
import com.parking.exception.InvalidOperationException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.factory.ParkingSlotFactory;
import com.parking.model.entity.Level;
import com.parking.model.entity.ParkingLot;
import com.parking.model.entity.ParkingSlot;
import com.parking.model.entity.ParkingTicket;
import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import com.parking.repository.LevelRepository;
import com.parking.repository.ParkingLotRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.ParkingTicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private ParkingLotRepository parkingLotRepository;
    @Mock private LevelRepository levelRepository;
    @Mock private ParkingSlotRepository parkingSlotRepository;
    @Mock private ParkingTicketRepository parkingTicketRepository;
    @Mock private ParkingSlotFactory parkingSlotFactory;

    @InjectMocks
    private AdminService adminService;

    private ParkingLot lot;
    private Level level;

    @BeforeEach
    void setUp() {
        lot = ParkingLot.builder().id(1L).name("Main Lot").levels(new ArrayList<>()).build();
        level = Level.builder().id(1L).floorNumber(1).parkingLot(lot).slots(new ArrayList<>()).build();
    }

    @Test
    void createParkingLot_success() {
        when(parkingLotRepository.save(any())).thenReturn(lot);
        ParkingLotResponse response = adminService.createParkingLot(new CreateParkingLotRequest("Main Lot"));
        assertEquals("Main Lot", response.name());
        assertEquals(1L, response.id());
    }

    @Test
    void getParkingLot_notFound() {
        when(parkingLotRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> adminService.getParkingLot(99L));
    }

    @Test
    void deleteParkingLot_withActiveSessions_throws() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(parkingTicketRepository.findByParkingSlotLevelParkingLotIdAndExitTimeIsNull(1L))
                .thenReturn(List.of(ParkingTicket.builder().id(1L).build()));

        assertThrows(InvalidOperationException.class, () -> adminService.deleteParkingLot(1L));
        verify(parkingLotRepository, never()).delete(any());
    }

    @Test
    void deleteParkingLot_noActiveSessions_success() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(parkingTicketRepository.findByParkingSlotLevelParkingLotIdAndExitTimeIsNull(1L))
                .thenReturn(Collections.emptyList());

        adminService.deleteParkingLot(1L);
        verify(parkingLotRepository).delete(lot);
    }

    @Test
    void addLevel_success() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(levelRepository.save(any())).thenReturn(level);

        LevelResponse response = adminService.addLevel(1L, new AddLevelRequest(1));
        assertEquals(1, response.floorNumber());
    }

    @Test
    void addSlot_success() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));
        ParkingSlot slot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.AVAILABLE).level(level).build();
        when(parkingSlotFactory.create(1, SlotType.COMPACT, level)).thenReturn(slot);
        when(parkingSlotRepository.save(any())).thenReturn(slot);

        SlotResponse response = adminService.addSlot(1L, 1L, new AddSlotRequest(1, SlotType.COMPACT));
        assertEquals(SlotType.COMPACT, response.slotType());
        assertEquals(1, response.slotNumber());
    }

    @Test
    void removeSlot_occupied_throws() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));
        ParkingSlot occupiedSlot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.OCCUPIED).level(level).build();
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(occupiedSlot));

        assertThrows(InvalidOperationException.class, () -> adminService.removeSlot(1L, 1L, 1L));
    }

    @Test
    void removeLevel_withOccupiedSlots_throws() {
        ParkingSlot occupiedSlot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.OCCUPIED).level(level).build();
        level.getSlots().add(occupiedSlot);
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));

        assertThrows(InvalidOperationException.class, () -> adminService.removeLevel(1L, 1L));
        verify(levelRepository, never()).delete(any());
    }

    @Test
    void updateSlotStatus_toMaintenance_success() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));
        ParkingSlot slot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.AVAILABLE).level(level).build();
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(slot));
        when(parkingSlotRepository.save(any())).thenReturn(slot);

        SlotResponse response = adminService.updateSlotStatus(1L, 1L, 1L, new UpdateSlotStatusRequest(SlotStatus.MAINTENANCE));
        assertEquals(SlotStatus.MAINTENANCE, response.status());
    }

    @Test
    void updateSlotStatus_toOccupied_throws() {
        assertThrows(InvalidOperationException.class,
                () -> adminService.updateSlotStatus(1L, 1L, 1L, new UpdateSlotStatusRequest(SlotStatus.OCCUPIED)));
    }

    @Test
    void updateSlotStatus_occupiedSlot_throws() {
        when(parkingLotRepository.findById(1L)).thenReturn(Optional.of(lot));
        when(levelRepository.findById(1L)).thenReturn(Optional.of(level));
        ParkingSlot occupiedSlot = ParkingSlot.builder().id(1L).slotNumber(1).slotType(SlotType.COMPACT).status(SlotStatus.OCCUPIED).level(level).build();
        when(parkingSlotRepository.findById(1L)).thenReturn(Optional.of(occupiedSlot));

        assertThrows(InvalidOperationException.class,
                () -> adminService.updateSlotStatus(1L, 1L, 1L, new UpdateSlotStatusRequest(SlotStatus.AVAILABLE)));
    }
}
