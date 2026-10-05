package com.bcs.rechnung.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development")
            ))
            .info(
                new Info()
                    .title("Rechnung Enterprise API")
                    .version("1.0.0")
                    .description("""
                                        Enterprise Rechnungssystem

                                        Funktionen:
                                        - Kundenverwaltung
                                        - Produktverwaltung
                                        - Firmenverwaltung
                                        - Bankkontoverwaltung
                                        - Rechnungserstellung
                                        - PDF-Generierung
                                        """)
                    .contact(
                        new Contact()
                            .name("BCS GmbH")
                            .email("support@bcs-gmbh.de")
                    )
                    .license(
                        new License()
                            .name("Commercial")
                    )
            );
    }
}
