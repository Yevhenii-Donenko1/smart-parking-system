package com.parking.service;

import com.parking.dto.request.*;
import com.parking.dto.response.*;
import com.parking.exception.InvalidOperationException;
import com.parking.exception.ResourceNotFoundException;
import com.parking.factory.ParkingSlotFactory;
import com.parking.model.entity.Level;
import com.parking.model.entity.ParkingLot;
import com.parking.model.entity.ParkingSlot;
import com.parking.model.enums.SlotStatus;
import com.parking.repository.LevelRepository;
import com.parking.repository.ParkingLotRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.repository.ParkingTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final ParkingLotRepository parkingLotRepository;
    private final LevelRepository levelRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingTicketRepository parkingTicketRepository;
    private final ParkingSlotFactory parkingSlotFactory;

    public ParkingLotResponse createParkingLot(CreateParkingLotRequest request) {
        ParkingLot lot = ParkingLot.builder()
                .name(request.name())
                .build();
        lot = parkingLotRepository.save(lot);
        return toResponse(lot);
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> getAllParkingLots() {
        return parkingLotRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParkingLotResponse getParkingLot(Long lotId) {
        return toResponse(findLot(lotId));
    }

    public void deleteParkingLot(Long lotId) {
        ParkingLot lot = findLot(lotId);
        boolean hasActiveSessions = !parkingTicketRepository
                .findByParkingSlotLevelParkingLotIdAndExitTimeIsNull(lotId).isEmpty();
        if (hasActiveSessions) {
            throw new InvalidOperationException("Cannot delete parking lot with active sessions");
        }
        parkingLotRepository.delete(lot);
    }

    public LevelResponse addLevel(Long lotId, AddLevelRequest request) {
        ParkingLot lot = findLot(lotId);
        Level level = Level.builder()
                .floorNumber(request.floorNumber())
                .parkingLot(lot)
                .build();
        level = levelRepository.save(level);
        return toLevelResponse(level);
    }

    public void removeLevel(Long lotId, Long levelId) {
        Level level = findLevel(lotId, levelId);
        boolean hasOccupiedSlots = level.getSlots().stream()
                .anyMatch(s -> s.getStatus() == SlotStatus.OCCUPIED);
        if (hasOccupiedSlots) {
            throw new InvalidOperationException("Cannot remove level with occupied slots");
        }
        levelRepository.delete(level);
    }

    public SlotResponse addSlot(Long lotId, Long levelId, AddSlotRequest request) {
        Level level = findLevel(lotId, levelId);
        ParkingSlot slot = parkingSlotFactory.create(request.slotNumber(), request.slotType(), level);
        slot = parkingSlotRepository.save(slot);
        return toSlotResponse(slot);
    }

    public void removeSlot(Long lotId, Long levelId, Long slotId) {
        ParkingSlot slot = findSlot(lotId, levelId, slotId);
        if (slot.getStatus() == SlotStatus.OCCUPIED) {
            throw new InvalidOperationException("Cannot remove an occupied slot");
        }
        parkingSlotRepository.delete(slot);
    }

    public SlotResponse updateSlotStatus(Long lotId, Long levelId, Long slotId, UpdateSlotStatusRequest request) {
        if (request.status() == SlotStatus.OCCUPIED) {
            throw new InvalidOperationException("Cannot manually set slot status to OCCUPIED");
        }
        ParkingSlot slot = findSlot(lotId, levelId, slotId);
        if (slot.getStatus() == SlotStatus.OCCUPIED) {
            throw new InvalidOperationException("Cannot change status of an occupied slot");
        }
        slot.setStatus(request.status());
        slot = parkingSlotRepository.save(slot);
        return toSlotResponse(slot);
    }

    private ParkingLot findLot(Long lotId) {
        return parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with id: " + lotId));
    }

    private Level findLevel(Long lotId, Long levelId) {
        findLot(lotId);
        return levelRepository.findById(levelId)
                .filter(l -> l.getParkingLot().getId().equals(lotId))
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + levelId));
    }

    private ParkingSlot findSlot(Long lotId, Long levelId, Long slotId) {
        findLevel(lotId, levelId);
        return parkingSlotRepository.findById(slotId)
                .filter(s -> s.getLevel().getId().equals(levelId))
                .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));
    }

    private ParkingLotResponse toResponse(ParkingLot lot) {
        List<LevelResponse> levels = lot.getLevels().stream()
                .map(this::toLevelResponse)
                .toList();
        return new ParkingLotResponse(lot.getId(), lot.getName(), levels);
    }

    private LevelResponse toLevelResponse(Level level) {
        List<SlotResponse> slots = level.getSlots().stream()
                .map(this::toSlotResponse)
                .toList();
        return new LevelResponse(level.getId(), level.getFloorNumber(), slots);
    }

    private SlotResponse toSlotResponse(ParkingSlot slot) {
        return new SlotResponse(slot.getId(), slot.getSlotNumber(), slot.getSlotType(), slot.getStatus());
    }
}
