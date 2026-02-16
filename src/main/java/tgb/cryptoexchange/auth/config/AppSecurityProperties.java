package tgb.cryptoexchange.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private List<String> ignoreUrls = new ArrayList<>();
}
