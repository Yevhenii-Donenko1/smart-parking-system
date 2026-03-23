@parking @check-out
Feature: Vehicle Check-Out
  As a parking system, I want to handle vehicle check-outs and fee calculation

  Background:
    Given a parking lot "Main Lot" exists
    And the lot has a level with floor number 1
    And the level has a "COMPACT" slot with number 1

  Scenario: Successful check-out with fee
    Given a vehicle "FEE-001" of type "CAR" is checked in
    When I check out vehicle "FEE-001"
    Then the response status should be 200
    And the check-out response should contain license plate "FEE-001"
    And the check-out response should contain a fee

  Scenario: Check-out non-parked vehicle
    When I check out vehicle "GHOST-001"
    Then the response status should be 404

  Scenario: Slot becomes available after check-out
    Given the level has a "COMPACT" slot with number 2
    And a vehicle "FREE-001" of type "CAR" is checked in to slot number 1
    When I check out vehicle "FREE-001"
    Then the response status should be 200
    And a new vehicle "FREE-002" of type "CAR" can check in successfully
