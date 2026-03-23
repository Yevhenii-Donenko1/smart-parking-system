package com.parking.cucumber.hooks;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseCleanupHook {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before(order = 0)
    public void cleanDatabase() {
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("TRUNCATE TABLE parking_tickets");
        jdbcTemplate.execute("TRUNCATE TABLE parking_slots");
        jdbcTemplate.execute("TRUNCATE TABLE levels");
        jdbcTemplate.execute("TRUNCATE TABLE vehicles");
        jdbcTemplate.execute("TRUNCATE TABLE parking_lots");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }
}
