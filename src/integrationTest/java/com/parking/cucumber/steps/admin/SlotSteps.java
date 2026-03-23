package com.parking.cucumber.steps.admin;

import com.parking.cucumber.ScenarioContext;
import com.parking.dto.request.AddSlotRequest;
import com.parking.dto.request.UpdateSlotStatusRequest;
import com.parking.model.enums.SlotStatus;
import com.parking.model.enums.SlotType;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SlotSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ScenarioContext context;

    @Given("the level has a {string} slot with number {int}")
    public void theLevelHasASlotWithNumber(String slotType, int slotNumber) {
        Long lotId = context.get("lotId");
        Long levelId = context.get("levelId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/admin/lots/" + lotId + "/levels/" + levelId + "/slots",
                new AddSlotRequest(slotNumber, SlotType.valueOf(slotType)),
                Map.class);
        assertEquals(201, response.getStatusCode().value());
        Long slotId = ((Number) response.getBody().get("id")).longValue();
        context.store("slotId", slotId);
    }

    @When("I add a {string} slot with number {int}")
    public void iAddASlotWithNumber(String slotType, int slotNumber) {
        Long lotId = context.get("lotId");
        Long levelId = context.get("levelId");
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/admin/lots/" + lotId + "/levels/" + levelId + "/slots",
                new AddSlotRequest(slotNumber, SlotType.valueOf(slotType)),
                Map.class);
        context.setLastResponse(response);
        if (response.getBody() != null && response.getBody().get("id") != null) {
            context.store("slotId", ((Number) response.getBody().get("id")).longValue());
        }
    }

    @When("I remove the slot")
    public void iRemoveTheSlot() {
        Long lotId = context.get("lotId");
        Long levelId = context.get("levelId");
        Long slotId = context.get("slotId");
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/admin/lots/" + lotId + "/levels/" + levelId + "/slots/" + slotId,
                HttpMethod.DELETE, null, Map.class);
        context.setLastResponse(response);
    }

    @When("I update the slot status to {string}")
    public void iUpdateTheSlotStatusTo(String status) {
        Long lotId = context.get("lotId");
        Long levelId = context.get("levelId");
        Long slotId = context.get("slotId");
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/v1/admin/lots/" + lotId + "/levels/" + levelId + "/slots/" + slotId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(new UpdateSlotStatusRequest(SlotStatus.valueOf(status))),
                Map.class);
        context.setLastResponse(response);
    }

    @And("the response should contain a slot with type {string} and number {int}")
    public void theResponseShouldContainASlotWithTypeAndNumber(String slotType, int slotNumber) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(slotType, body.get("slotType"));
        assertEquals(slotNumber, ((Number) body.get("slotNumber")).intValue());
    }

    @And("the response should contain a slot with status {string}")
    public void theResponseShouldContainASlotWithStatus(String status) {
        Map body = (Map) context.getLastResponse().getBody();
        assertNotNull(body);
        assertEquals(status, body.get("status"));
    }
}
