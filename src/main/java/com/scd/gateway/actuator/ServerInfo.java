package com.scd.gateway.actuator;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "info")
@Data
public class ServerInfo {
    private Map<String, String> app;
}
