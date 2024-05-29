package com.app.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt.auth.converter")
public class KeycloakConfiguration {
    private String resourceId;
    private String principleAttribute;
}
