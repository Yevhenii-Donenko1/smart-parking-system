package com.parking.cucumber;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@io.cucumber.spring.ScenarioScope
public class ScenarioContext {

    private ResponseEntity<?> lastResponse;
    private final Map<String, Object> storedValues = new HashMap<>();

    public ResponseEntity<?> getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(ResponseEntity<?> lastResponse) {
        this.lastResponse = lastResponse;
    }

    public void store(String key, Object value) {
        storedValues.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) storedValues.get(key);
    }
}
