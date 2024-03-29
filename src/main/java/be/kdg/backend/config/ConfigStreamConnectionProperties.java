package be.kdg.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "app.stream-connection")
public class ConfigStreamConnectionProperties {
    private String httpUrl;
    private String rtmpUrl;
    private String httpUrlClient;
    private String rtmpUrlClient;
    private String rtmpToken;
    private String username;
    private String password;
}
