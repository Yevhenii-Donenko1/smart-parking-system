package com.parking.strategy.fee;

import com.parking.model.enums.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HourlyFeeStrategyTest {

    private HourlyFeeStrategy strategy;
    private LocalDateTime baseTime;

    @BeforeEach
    void setUp() {
        strategy = new HourlyFeeStrategy();
        baseTime = LocalDateTime.of(2026, 3, 19, 10, 0);
    }

    @Test
    void exactOneHour_car() {
        BigDecimal fee = strategy.calculateFee(VehicleType.CAR, baseTime, baseTime.plusHours(1));
        assertEquals(new BigDecimal("2.00"), fee);
    }

    @Test
    void exactTwoHours_car() {
        BigDecimal fee = strategy.calculateFee(VehicleType.CAR, baseTime, baseTime.plusHours(2));
        assertEquals(new BigDecimal("4.00"), fee);
    }

    @Test
    void partialHour_roundsUp_car() {
        BigDecimal fee = strategy.calculateFee(VehicleType.CAR, baseTime, baseTime.plusMinutes(90));
        assertEquals(new BigDecimal("4.00"), fee);
    }

    @Test
    void fewMinutes_chargedOneHour_car() {
        BigDecimal fee = strategy.calculateFee(VehicleType.CAR, baseTime, baseTime.plusMinutes(15));
        assertEquals(new BigDecimal("2.00"), fee);
    }

    @Test
    void zeroDuration_chargedOneHour() {
        BigDecimal fee = strategy.calculateFee(VehicleType.CAR, baseTime, baseTime);
        assertEquals(new BigDecimal("2.00"), fee);
    }

    @Test
    void twentyFourHours_car() {
        BigDecimal fee = strategy.calculateFee(VehicleType.CAR, baseTime, baseTime.plusHours(24));
        assertEquals(new BigDecimal("48.00"), fee);
    }

    @Test
    void motorcycle_rate() {
        BigDecimal fee = strategy.calculateFee(VehicleType.MOTORCYCLE, baseTime, baseTime.plusHours(3));
        assertEquals(new BigDecimal("3.00"), fee);
    }

    @Test
    void truck_rate() {
        BigDecimal fee = strategy.calculateFee(VehicleType.TRUCK, baseTime, baseTime.plusHours(2));
        assertEquals(new BigDecimal("6.00"), fee);
    }

    @Test
    void partialHour_motorcycle() {
        BigDecimal fee = strategy.calculateFee(VehicleType.MOTORCYCLE, baseTime, baseTime.plusMinutes(61));
        assertEquals(new BigDecimal("2.00"), fee);
    }
}
