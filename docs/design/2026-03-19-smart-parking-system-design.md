# Smart Parking System - Design Document

## Architecture Overview

The system follows a **flat layered architecture** with Spring Boot 3.x:

```
controller/        REST API layer (AdminController, ParkingController)
    |
service/           Business logic (AdminService, ParkingService)
    |
repository/        Data access (Spring Data JPA repositories)
    |
model/             Domain model
  entity/          JPA entities (ParkingLot, Level, ParkingSlot, Vehicle, ParkingTicket)
  enums/           Type enums (VehicleType, SlotType, SlotStatus)
    |
strategy/          Pluggable algorithms
  fee/             Fee calculation strategies
  assignment/      Slot assignment strategies
    |
dto/               Data transfer objects
  request/         API request records
  response/        API response records
    |
exception/         Custom exceptions + GlobalExceptionHandler
```

## Domain Model

### Entities and Relationships

```
ParkingLot (1) --- (*) Level (1) --- (*) ParkingSlot
                                              |
Vehicle (1) --- (*) ParkingTicket (*) --------+
```

- **ParkingLot**: Top-level container with a unique name, contains levels.
- **Level**: A floor within a lot, identified by `floorNumber`. Unique constraint on `(parkingLot, floorNumber)`.
- **ParkingSlot**: A parking space on a level with a `slotType` and `status`. Unique constraint on `(level, slotNumber)`.
- **Vehicle**: Identified by `licensePlate`, has a `vehicleType`. Created on first check-in.
- **ParkingTicket**: Links a vehicle to a slot for a parking session. Active ticket = `exitTime IS NULL`.

### Enums

| Enum | Values | Purpose |
|------|--------|---------|
| `VehicleType` | MOTORCYCLE, CAR, TRUCK | Vehicle classification with compatible slot types |
| `SlotType` | MOTORCYCLE, COMPACT, LARGE, HANDICAPPED | Slot classification |
| `SlotStatus` | AVAILABLE, OCCUPIED, MAINTENANCE | Slot availability state |

### Compatibility Matrix (Flexible Upsizing)

| Vehicle \ Slot | MOTORCYCLE | COMPACT | LARGE | HANDICAPPED |
|---------------|:---:|:---:|:---:|:---:|
| MOTORCYCLE    | Y | Y | Y | N |
| CAR           | N | Y | Y | Y |
| TRUCK         | N | N | Y | N |

## API Design

### Admin API (`/api/v1/admin/lots`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/lots` | Create parking lot |
| GET | `/lots` | List all lots |
| GET | `/lots/{lotId}` | Get lot details |
| DELETE | `/lots/{lotId}` | Delete lot (fails if active sessions) |
| POST | `/lots/{lotId}/levels` | Add level |
| DELETE | `/lots/{lotId}/levels/{levelId}` | Remove level |
| POST | `/lots/{lotId}/levels/{levelId}/slots` | Add slot |
| DELETE | `/lots/{lotId}/levels/{levelId}/slots/{slotId}` | Remove slot |
| PATCH | `/lots/{lotId}/levels/{levelId}/slots/{slotId}/status` | Update slot status |

### Parking API (`/api/v1/parking/lots/{lotId}`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/check-in` | Check in a vehicle |
| POST | `/check-out` | Check out a vehicle |
| GET | `/sessions` | View active sessions |

## Design Patterns

### Strategy Pattern - Fee Calculation
- **Interface**: `FeeStrategy` with `calculateFee(vehicleType, entry, exit)`
- **Implementation**: `HourlyFeeStrategy` with per-vehicle-type hourly rates
- **Extensibility**: Add new strategies (e.g., flat rate, dynamic pricing) by implementing the interface

### Strategy Pattern - Slot Assignment
- **Interface**: `SlotAssignmentStrategy` with `findSlot(lotId, vehicleType)`
- **Implementation**: `NearestSlotStrategy` (lowest floor, lowest slot number)
- **Extensibility**: Add strategies like random assignment, priority-based, etc.

### Factory Pattern - Vehicle Creation
- Vehicles are created on-demand during check-in (get-or-create pattern)
- `VehicleType` enum encapsulates slot compatibility logic

## Extensibility Points

1. **New vehicle types**: Add to `VehicleType` enum with compatible slot types
2. **New slot types**: Add to `SlotType` enum, update compatibility in `VehicleType`
3. **Fee models**: Implement `FeeStrategy` interface, swap via Spring configuration
4. **Assignment algorithms**: Implement `SlotAssignmentStrategy` interface
5. **New operations**: Add endpoints to controllers, service methods as needed

## Testing Strategy

- **Unit tests**: JUnit 5 + Mockito for strategies, services, and enum logic
- **Integration tests**: Cucumber BDD with TestRestTemplate for full API flows
- **Database**: H2 in-memory, cleaned between Cucumber scenarios

## Decisions Log

| Decision | Rationale |
|----------|-----------|
| Java 17 | Required by Spring Boot 3.x, modern language features |
| Flexible upsizing | Motorcycle can park in compact/large slots for better utilization |
| Hourly fee rounding (ceil) | Partial hours charged as full hours, minimum 1 hour |
| Nearest-available default | Deterministic, lowest floor + slot number for predictable assignment |
| Pessimistic locking on slot query | Prevents race conditions in concurrent check-ins |
| Java records for DTOs | Clean, immutable, no boilerplate |
| H2 in-memory database | Simple setup, no external dependencies for demo |
