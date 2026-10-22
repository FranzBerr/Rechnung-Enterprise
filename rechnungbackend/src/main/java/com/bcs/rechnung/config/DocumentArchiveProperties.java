package com.bcs.rechnung.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(
    prefix = "app.document.archive"
)
public record DocumentArchiveProperties(
    Path directory
) {
}
