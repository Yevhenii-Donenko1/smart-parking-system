@admin
Feature: Slot Management
  As an admin, I want to manage parking slots

  Background:
    Given a parking lot "Test Lot" exists
    And the lot has a level with floor number 1

  Scenario: Add a slot to a level
    When I add a "COMPACT" slot with number 1
    Then the response status should be 201
    And the response should contain a slot with type "COMPACT" and number 1

  Scenario: Remove an available slot
    Given the level has a "COMPACT" slot with number 1
    When I remove the slot
    Then the response status should be 204

  Scenario: Toggle slot to maintenance
    Given the level has a "COMPACT" slot with number 1
    When I update the slot status to "MAINTENANCE"
    Then the response status should be 200
    And the response should contain a slot with status "MAINTENANCE"

  Scenario: Cannot remove an occupied slot
    Given the level has a "COMPACT" slot with number 1
    And a vehicle "OCC-001" of type "CAR" is checked in
    When I remove the slot
    Then the response status should be 400
