package be.kdg.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.stream-options")
public class ConfigStreamOptionsProperties {
    private int threadQueueSize;
    private int analyzeDuration;
    private int hlsTime;
    private int hlsListSize;
    private int hlsDeleteThreshold;
    private int masterPlPublishRate;
    private int maxFileAgeSeconds;
    private int reconnectDelaySeconds;
    private int staleTimeoutSeconds;
}
