package com.parking.cucumber.steps.parking;

import com.parking.cucumber.ScenarioContext;
import com.parking.dto.request.CheckInRequest;
import com.parking.dto.request.CheckOutRequest;
import com.parking.model.enums.VehicleType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CheckOutSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScenarioContext context;

    @When("I check out vehicle {string}")
    public void iCheckOutVehicle(String licensePlate) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/parking/lots/" + lotId + "/check-out",
                new CheckOutRequest(licensePlate),
                Map.class);
        context.setLastResponse(response);
    }

    @And("the check-out response should contain license plate {string}")
    public void theCheckOutResponseShouldContainLicensePlate(String licensePlate) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(licensePlate, body.get("licensePlate"));
    }

    @And("the check-out response should contain a fee")
    public void theCheckOutResponseShouldContainAFee() {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertNotNull(body.get("fee"));
    }

    @And("a new vehicle {string} of type {string} can check in successfully")
    public void aNewVehicleCanCheckInSuccessfully(String licensePlate, String vehicleType) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/parking/lots/" + lotId + "/check-in",
                new CheckInRequest(licensePlate, VehicleType.valueOf(vehicleType)),
                Map.class);
        assertEquals(201, response.getStatusCode().value());
    }
}
