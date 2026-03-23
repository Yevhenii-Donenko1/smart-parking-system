@parking @check-in
Feature: Vehicle Check-In
  As a parking system, I want to handle vehicle check-ins

  Background:
    Given a parking lot "Main Lot" exists
    And the lot has a level with floor number 1

  Scenario: Successful car check-in
    Given the level has a "COMPACT" slot with number 1
    When I check in vehicle "CAR-001" of type "CAR"
    Then the response status should be 201
    And the check-in response should contain license plate "CAR-001"
    And the check-in response should contain slot type "COMPACT"

  Scenario: Vehicle already parked
    Given the level has a "COMPACT" slot with number 1
    And a vehicle "DUP-001" of type "CAR" is checked in
    When I check in vehicle "DUP-001" of type "CAR"
    Then the response status should be 409

  Scenario: No compatible slot available
    Given the level has a "MOTORCYCLE" slot with number 1
    When I check in vehicle "BIG-001" of type "TRUCK"
    Then the response status should be 409

  Scenario: Motorcycle uses compact slot (flexible upsizing)
    Given the level has a "COMPACT" slot with number 1
    When I check in vehicle "MOTO-001" of type "MOTORCYCLE"
    Then the response status should be 201
    And the check-in response should contain slot type "COMPACT"

  Scenario Outline: Different vehicle types check-in
    Given the level has a "<slotType>" slot with number 1
    When I check in vehicle "<plate>" of type "<vehicleType>"
    Then the response status should be 201

    Examples:
      | vehicleType | slotType   | plate    |
      | MOTORCYCLE  | MOTORCYCLE | M-001    |
      | CAR         | COMPACT    | C-001    |
      | TRUCK       | LARGE      | T-001    |
      | CAR         | HANDICAPPED| H-001    |
