package com.parking.strategy.fee;

import com.parking.model.enums.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class HourlyFeeStrategy implements FeeStrategy {

    private static final Map<VehicleType, BigDecimal> HOURLY_RATES = Map.of(
            VehicleType.MOTORCYCLE, new BigDecimal("1.00"),
            VehicleType.CAR, new BigDecimal("2.00"),
            VehicleType.TRUCK, new BigDecimal("3.00")
    );

    @Override
    public BigDecimal calculateFee(VehicleType vehicleType, LocalDateTime entry, LocalDateTime exit) {
        long minutes = Duration.between(entry, exit).toMinutes();
        long hours = (long) Math.ceil(minutes / 60.0);
        if (hours == 0) {
            hours = 1;
        }
        BigDecimal rate = HOURLY_RATES.get(vehicleType);
        return rate.multiply(BigDecimal.valueOf(hours));
    }
}
