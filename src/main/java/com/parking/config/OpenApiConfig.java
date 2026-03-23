package com.parking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartParkingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Parking System API")
                        .description("REST API for managing parking lots, vehicle check-in/check-out, and fee calculation. "
                                + "Supports flexible slot assignment with upsizing (e.g., motorcycles can park in compact slots) "
                                + "and extensible fee calculation via the Strategy pattern.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Smart Parking Team")));
    }
}
