# Assignment: Smart Parking System

## Overview

Design and implement a Smart Parking System backend. The system should allow for the management of parking lots, handle vehicle check-in and check-out, and compute parking fees. The implementation must emphasize object-oriented principles, clean design, and extensibility.

## Definitions

### Parking Lot

A Parking Lot is a managed space containing:

- A unique identifier and name.
- One or more levels (physical floors).
- Each level contains a number of parking slots.

### Parking Slot (Spot)

A Parking Slot:

- Belongs to a specific level within a parking lot.
- Has a type (e.g., COMPACT, LARGE, MOTORCYCLE, HANDICAPPED).
- Can be either available or occupied.
- Has a unique ID within its level.

### Vehicles

Each vehicle:

- Has a license plate (unique identifier).
- Belongs to a vehicle type (e.g., CAR, MOTORCYCLE, TRUCK).

Vehicle type may influence:

- Slot compatibility (e.g., a truck can't park in a compact slot).
- Fee calculation (different base rates or pricing models).

## Vehicle Check-in / Check-out (via API)

### Check-in Flow

- The user calls an API endpoint to check-in a vehicle.
- The system assigns an available slot compatible with the vehicle type.
- A ticket/receipt is generated with:
  - Vehicle details.
  - Entry timestamp.
  - Assigned slot and level.

### Check-out Flow

- The user calls an API endpoint to check-out a vehicle.
- The system:
  - Marks the slot as available.
  - Calculates the parking fee based on:
    - Duration of stay (entry -> exit).
    - Vehicle type.
  - Returns a summary with:
    - Entry/exit time.
    - Total duration.
    - Fee.

## Core Functional Requirements

### Parking Lot Management (Admin API)

- Create/delete a parking lot.
- Add/remove levels to a parking lot.
- Add/remove parking slots to a level.
- Mark a parking slot as available/unavailable manually (e.g., for maintenance).

### Vehicle Check-in/Check-out API

- Check-in a vehicle (assign a suitable available slot).
- Check-out a vehicle (free the slot, calculate and return fee).
- View current parking sessions (active check-ins).

### Fee Calculation

- Different base rates per vehicle type (e.g., $1/hr for motorcycles, $2/hr for cars, $3/hr for trucks).
- Should be designed in a way that adding new rules is easy (Strategy Pattern is a good fit here).

## Technical Constraints

- **Language:** Java 17+
- **Framework:** Spring Boot (3.x)
- **Build Tool:** Gradle
- **Database:** In-memory (H2 or in-memory repository abstraction)
- **API:** RESTful (Spring Web)
- **Architecture:** Clean, modular architecture with well-separated layers:
  - Domain / Service / Controller / Repository layers

## Evaluation Criteria

| Area | What We Look For |
|------|-----------------|
| OOP Design | Good modeling of real-world concepts, use of abstraction/inheritance |
| Design Patterns | Appropriate use of patterns like Strategy (for pricing), Factory (for vehicle/slot creation) |
| Code Structure | Modular, clean, maintainable structure |
| API Design | RESTful principles, clarity, versioning if needed |
| Extensibility | Ability to add new slot types, fee models, or vehicle types |
| Testing | At least basic unit and service-level tests (JUnit, MockMVC, etc.) |
| Documentation | README with design rationale, instructions, and examples |

## Submission Guidelines

- Upload your code to GitHub (public or private repo with shared access).
- Include a README.md with:
  - Overview of your solution and assumptions.
  - How to build and run the project.
  - Example API calls (can be Postman or curl).
  - Known limitations or TODOs.