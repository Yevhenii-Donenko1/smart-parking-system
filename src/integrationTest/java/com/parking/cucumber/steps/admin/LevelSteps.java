package com.parking.cucumber.steps.admin;

import com.parking.cucumber.ScenarioContext;
import com.parking.dto.request.AddLevelRequest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LevelSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScenarioContext context;

    @Given("the lot has a level with floor number {int}")
    public void theLotHasALevelWithFloorNumber(int floorNumber) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/admin/lots/" + lotId + "/levels",
                new AddLevelRequest(floorNumber),
                Map.class);
        assertEquals(201, response.getStatusCode().value());
        Long levelId = ((Number) response.getBody().get("id")).longValue();
        context.store("levelId", levelId);
    }

    @When("I add a level with floor number {int}")
    public void iAddALevelWithFloorNumber(int floorNumber) {
        Long lotId = context.get("lotId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/admin/lots/" + lotId + "/levels",
                new AddLevelRequest(floorNumber),
                Map.class);
        context.setLastResponse(response);
        if (response.getBody() != null && response.getBody().get("id") != null) {
            context.store("levelId", ((Number) response.getBody().get("id")).longValue());
        }
    }

    @When("I remove the level")
    public void iRemoveTheLevel() {
        Long lotId = context.get("lotId");
        Long levelId = context.get("levelId");
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/admin/lots/" + lotId + "/levels/" + levelId,
                HttpMethod.DELETE, null, Map.class);
        context.setLastResponse(response);
    }

    @And("the response should contain a level with floor number {int}")
    public void theResponseShouldContainALevelWithFloorNumber(int floorNumber) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(floorNumber, ((Number) body.get("floorNumber")).intValue());
    }
}
