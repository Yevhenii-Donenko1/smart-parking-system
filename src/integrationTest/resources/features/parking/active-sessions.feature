@parking
Feature: Active Parking Sessions
  As a parking system, I want to view active parking sessions

  Background:
    Given a parking lot "Main Lot" exists
    And the lot has a level with floor number 1

  Scenario: No active sessions
    Given the level has a "COMPACT" slot with number 1
    When I view active sessions
    Then the response status should be 200
    And there should be 0 active sessions

  Scenario: One active session
    Given the level has a "COMPACT" slot with number 1
    And a vehicle "ACT-001" of type "CAR" is checked in
    When I view active sessions
    Then the response status should be 200
    And there should be 1 active sessions

  Scenario: Multiple active sessions
    Given the level has a "COMPACT" slot with number 1
    And the level has a "COMPACT" slot with number 2
    And a vehicle "ACT-001" of type "CAR" is checked in
    And a vehicle "ACT-002" of type "CAR" is checked in
    When I view active sessions
    Then the response status should be 200
    And there should be 2 active sessions

  Scenario: Checked-out vehicle not shown
    Given the level has a "COMPACT" slot with number 1
    And a vehicle "DONE-001" of type "CAR" is checked in
    And vehicle "DONE-001" is checked out
    When I view active sessions
    Then the response status should be 200
    And there should be 0 active sessions
