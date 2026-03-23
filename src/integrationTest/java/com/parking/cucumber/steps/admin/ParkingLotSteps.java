package com.parking.cucumber.steps.admin;

import com.parking.cucumber.ScenarioContext;
import com.parking.dto.request.CreateParkingLotRequest;
import com.parking.dto.response.ParkingLotResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ParkingLotSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScenarioContext context;

    private static final String ADMIN_LOTS_URL = "/api/v1/admin/lots";

    @Given("a parking lot {string} exists")
    public void aParkingLotExists(String name) {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                ADMIN_LOTS_URL,
                new CreateParkingLotRequest(name),
                Map.class);
        assertEquals(201, response.getStatusCode().value());
        Long lotId = ((Number) response.getBody().get("id")).longValue();
        context.store("lotId", lotId);
    }

    @When("I create a parking lot with name {string}")
    public void iCreateAParkingLotWithName(String name) {
        ResponseEntity<Map> response = restTemplate.postForEntity(
                ADMIN_LOTS_URL,
                new CreateParkingLotRequest(name),
                Map.class);
        context.setLastResponse(response);
        if (response.getBody() != null && response.getBody().get("id") != null) {
            context.store("lotId", ((Number) response.getBody().get("id")).longValue());
        }
    }

    @When("I get the parking lot")
    public void iGetTheParkingLot() {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.getForEntity(
                ADMIN_LOTS_URL + "/" + lotId, Map.class);
        context.setLastResponse(response);
    }

    @When("I list all parking lots")
    public void iListAllParkingLots() {
        ResponseEntity<List> response = restTemplate.getForEntity(ADMIN_LOTS_URL, List.class);
        context.setLastResponse(response);
    }

    @When("I delete the parking lot")
    public void iDeleteTheParkingLot() {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.exchange(
                ADMIN_LOTS_URL + "/" + lotId, HttpMethod.DELETE, null, Map.class);
        context.setLastResponse(response);
    }

    @And("the response should contain a parking lot with name {string}")
    public void theResponseShouldContainAParkingLotWithName(String name) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(name, body.get("name"));
    }

    @And("the response should contain {int} parking lots")
    public void theResponseShouldContainParkingLots(int count) {
        List body = (List) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(count, body.size());
    }
}
