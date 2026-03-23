package com.parking.model.entity;

import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_slots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"level_id", "slot_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SlotStatus status = SlotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;
}
