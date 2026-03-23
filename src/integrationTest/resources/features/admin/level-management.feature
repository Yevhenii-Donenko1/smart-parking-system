@admin
Feature: Level Management
  As an admin, I want to manage levels in a parking lot

  Background:
    Given a parking lot "Test Lot" exists

  Scenario: Add a level to a parking lot
    When I add a level with floor number 1
    Then the response status should be 201
    And the response should contain a level with floor number 1

  Scenario: Remove a level from a parking lot
    Given the lot has a level with floor number 1
    When I remove the level
    Then the response status should be 204
