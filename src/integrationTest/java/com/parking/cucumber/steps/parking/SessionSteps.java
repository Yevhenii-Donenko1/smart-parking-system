package com.parking.cucumber.steps.parking;

import com.parking.cucumber.ScenarioContext;
import com.parking.dto.request.CheckOutRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScenarioContext context;

    @Given("vehicle {string} is checked out")
    public void vehicleIsCheckedOut(String licensePlate) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/parking/lots/" + lotId + "/check-out",
                new CheckOutRequest(licensePlate),
                Map.class);
        assertEquals(200, response.getStatusCode().value());
    }

    @When("I view active sessions")
    public void iViewActiveSessions() {
        Long lotId = context.get("lotId");
        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/v1/parking/lots/" + lotId + "/sessions",
                List.class);
        context.setLastResponse(response);
    }

    @And("there should be {int} active sessions")
    public void thereShouldBeActiveSessions(int count) {
        List body = (List) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(count, body.size());
    }
}
