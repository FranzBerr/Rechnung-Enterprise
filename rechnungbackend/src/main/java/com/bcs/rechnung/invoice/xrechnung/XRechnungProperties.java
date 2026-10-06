package com.bcs.rechnung.invoice.xrechnung;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "xrechnung")
public class XRechnungProperties {

    private String outputDirectory;

    @PostConstruct
    public void validate() {
        if (!StringUtils.hasText(outputDirectory)) {
            throw new IllegalStateException(
                "Die Konfiguration "
                    + "'xrechnung.output-directory' "
                    + "fehlt oder ist leer."
            );
        }
    }
}
