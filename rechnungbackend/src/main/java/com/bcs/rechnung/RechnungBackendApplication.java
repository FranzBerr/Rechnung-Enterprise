package com.bcs.rechnung;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RechnungBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(
            RechnungBackendApplication.class,
            args
        );
    }
}
