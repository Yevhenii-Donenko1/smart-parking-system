package com.parking.repository;

import com.parking.model.entity.ParkingTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingTicketRepository extends JpaRepository<ParkingTicket, Long> {

    Optional<ParkingTicket> findByVehicleLicensePlateAndExitTimeIsNull(String licensePlate);

    List<ParkingTicket> findByParkingSlotLevelParkingLotIdAndExitTimeIsNull(Long lotId);
}
