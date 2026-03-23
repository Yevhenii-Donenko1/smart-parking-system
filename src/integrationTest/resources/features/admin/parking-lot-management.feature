@admin
Feature: Parking Lot Management
  As an admin, I want to manage parking lots

  Scenario: Create a parking lot
    When I create a parking lot with name "Downtown Garage"
    Then the response status should be 201
    And the response should contain a parking lot with name "Downtown Garage"

  Scenario: Get a parking lot
    Given a parking lot "Airport Lot" exists
    When I get the parking lot
    Then the response status should be 200
    And the response should contain a parking lot with name "Airport Lot"

  Scenario: List all parking lots
    Given a parking lot "Lot A" exists
    And a parking lot "Lot B" exists
    When I list all parking lots
    Then the response status should be 200
    And the response should contain 2 parking lots

  Scenario: Delete a parking lot
    Given a parking lot "Temp Lot" exists
    When I delete the parking lot
    Then the response status should be 204

  Scenario: Delete a parking lot with active sessions should fail
    Given a parking lot "Busy Lot" exists
    And the lot has a level with floor number 1
    And the level has a "COMPACT" slot with number 1
    And a vehicle "BUSY-001" of type "CAR" is checked in
    When I delete the parking lot
    Then the response status should be 400
