package com.parking.repository;

import com.parking.model.entity.ParkingSlot;
import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ParkingSlot s JOIN s.level l " +
            "WHERE l.parkingLot.id = :lotId " +
            "AND s.slotType IN :types " +
            "AND s.status = :status " +
            "ORDER BY l.floorNumber ASC, s.slotNumber ASC")
    List<ParkingSlot> findAvailableSlotsByLotAndCompatibleTypes(
            @Param("lotId") Long lotId,
            @Param("types") Set<SlotType> types,
            @Param("status") SlotStatus status);
}
