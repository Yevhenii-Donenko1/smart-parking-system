package com.parking.cucumber.steps;

import com.parking.cucumber.ScenarioContext;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommonSteps {

    @Autowired
    private ScenarioContext context;

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertNotNull(context.getLastResponse(), "No response recorded");
        assertEquals(expectedStatus, context.getLastResponse().getStatusCode().value());
    }
}
