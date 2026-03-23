# Smart Parking System

A backend system for managing parking lots, handling vehicle check-in/check-out, and computing parking fees. Built with Spring Boot 3.x, Java 17, and H2 in-memory database.

## Design Rationale

- **Strategy Pattern** for fee calculation and slot assignment - allows easy swapping of algorithms
- **Factory Pattern** for vehicle and slot creation - encapsulates creation logic
- **Flexible upsizing** - smaller vehicles can park in larger slots (motorcycle in compact, car in large)
- **Nearest-available slot assignment** - deterministic assignment by lowest floor and slot number
- **Per-hour fee rounding** with `Math.ceil` - partial hours charged as full hours, minimum 1 hour
- **Pessimistic locking** on slot queries to prevent race conditions in concurrent check-ins
- **Java records** for DTOs - clean, immutable request/response objects
- **Request validation** with Bean Validation annotations (`@NotBlank`, `@NotNull`, `@Min`)

## Architecture

```
Controller Layer    ->  Service Layer  ->  Repository Layer  ->  H2 Database
(REST endpoints)       (business logic)   (Spring Data JPA)

                        Strategy Layer
                        (fee calc, slot assignment)
```

## Prerequisites

- Java 17+

## Build and Run

```bash
# Build (runs all tests)
./gradlew clean build

# Run the application
./gradlew bootRun
```

The application starts on `http://localhost:8080`.

**Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
**OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
**H2 Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:parkingdb`)

## API Documentation

### Admin API

#### Create Parking Lot
```bash
curl -X POST http://localhost:8080/api/v1/admin/lots \
  -H 'Content-Type: application/json' \
  -d '{"name": "Main Lot"}'
```

#### List All Lots
```bash
curl http://localhost:8080/api/v1/admin/lots
```

#### Get Parking Lot
```bash
curl http://localhost:8080/api/v1/admin/lots/1
```

#### Delete Parking Lot
```bash
curl -X DELETE http://localhost:8080/api/v1/admin/lots/1
```

#### Add Level
```bash
curl -X POST http://localhost:8080/api/v1/admin/lots/1/levels \
  -H 'Content-Type: application/json' \
  -d '{"floorNumber": 1}'
```

#### Remove Level
```bash
curl -X DELETE http://localhost:8080/api/v1/admin/lots/1/levels/1
```

#### Add Slot
```bash
curl -X POST http://localhost:8080/api/v1/admin/lots/1/levels/1/slots \
  -H 'Content-Type: application/json' \
  -d '{"slotNumber": 1, "slotType": "COMPACT"}'
```

Slot types: `MOTORCYCLE`, `COMPACT`, `LARGE`, `HANDICAPPED`

#### Remove Slot
```bash
curl -X DELETE http://localhost:8080/api/v1/admin/lots/1/levels/1/slots/1
```

#### Update Slot Status (Maintenance Toggle)
```bash
curl -X PATCH http://localhost:8080/api/v1/admin/lots/1/levels/1/slots/1/status \
  -H 'Content-Type: application/json' \
  -d '{"status": "MAINTENANCE"}'
```

### Parking API

#### Check In
```bash
curl -X POST http://localhost:8080/api/v1/parking/lots/1/check-in \
  -H 'Content-Type: application/json' \
  -d '{"licensePlate": "ABC-123", "vehicleType": "CAR"}'
```

Response:
```json
{
  "ticketId": 1,
  "licensePlate": "ABC-123",
  "floorNumber": 1,
  "slotNumber": 1,
  "slotType": "COMPACT",
  "entryTime": "2026-03-19T10:00:00"
}
```

#### Check Out
```bash
curl -X POST http://localhost:8080/api/v1/parking/lots/1/check-out \
  -H 'Content-Type: application/json' \
  -d '{"licensePlate": "ABC-123"}'
```

Response:
```json
{
  "ticketId": 1,
  "licensePlate": "ABC-123",
  "entryTime": "2026-03-19T10:00:00",
  "exitTime": "2026-03-19T12:30:00",
  "durationHours": 3,
  "fee": 6.00
}
```

#### View Active Sessions
```bash
curl http://localhost:8080/api/v1/parking/lots/1/sessions
```

## Fee Rates

| Vehicle Type | Rate |
|-------------|------|
| MOTORCYCLE | $1/hour |
| CAR | $2/hour |
| TRUCK | $3/hour |

Partial hours are rounded up. Minimum charge is 1 hour.

## Slot Compatibility

| Vehicle | Compatible Slots |
|---------|-----------------|
| MOTORCYCLE | MOTORCYCLE, COMPACT, LARGE |
| CAR | COMPACT, LARGE, HANDICAPPED |
| TRUCK | LARGE |

## Testing

```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew test --tests "com.parking.strategy.*" --tests "com.parking.service.*" --tests "com.parking.model.*"

# Run Cucumber BDD tests only
./gradlew test --tests "com.parking.cucumber.CucumberTest"
```

## Known Limitations and TODOs

- No authentication/authorization
- No pagination on list endpoints
- Fee rates are hardcoded (could be externalized to config or database)
- Single assignment strategy (nearest-available) - extensible via Strategy pattern
