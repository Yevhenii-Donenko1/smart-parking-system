package com.parking.cucumber.steps.parking;

import com.parking.cucumber.ScenarioContext;
import com.parking.dto.request.CheckInRequest;
import com.parking.model.enums.VehicleType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CheckInSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScenarioContext context;

    @Given("a vehicle {string} of type {string} is checked in")
    public void aVehicleIsCheckedIn(String licensePlate, String vehicleType) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/parking/lots/" + lotId + "/check-in",
                new CheckInRequest(licensePlate, VehicleType.valueOf(vehicleType)),
                Map.class);
        assertEquals(201, response.getStatusCode().value());
    }

    @Given("a vehicle {string} of type {string} is checked in to slot number {int}")
    public void aVehicleIsCheckedInToSlotNumber(String licensePlate, String vehicleType, int slotNumber) {
        aVehicleIsCheckedIn(licensePlate, vehicleType);
    }

    @When("I check in vehicle {string} of type {string}")
    public void iCheckInVehicle(String licensePlate, String vehicleType) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/parking/lots/" + lotId + "/check-in",
                new CheckInRequest(licensePlate, VehicleType.valueOf(vehicleType)),
                Map.class);
        context.setLastResponse(response);
    }

    @And("the check-in response should contain license plate {string}")
    public void theCheckInResponseShouldContainLicensePlate(String licensePlate) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(licensePlate, body.get("licensePlate"));
    }

    @And("the check-in response should contain slot type {string}")
    public void theCheckInResponseShouldContainSlotType(String slotType) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(slotType, body.get("slotType"));
    }
}
