package com.parking.factory;

import com.parking.model.entity.Vehicle;
import com.parking.model.enums.VehicleType;
import com.parking.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehicleFactory {

    private final VehicleRepository vehicleRepository;

    public Vehicle getOrCreate(String licensePlate, VehicleType vehicleType) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> vehicleRepository.save(
                        Vehicle.builder()
                                .licensePlate(licensePlate)
                                .vehicleType(vehicleType)
                                .build()));
    }
}
