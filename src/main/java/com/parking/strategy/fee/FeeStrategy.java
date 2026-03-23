package com.parking.strategy.fee;

import com.parking.model.enums.VehicleType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FeeStrategy {
    BigDecimal calculateFee(VehicleType vehicleType, LocalDateTime entry, LocalDateTime exit);
}
