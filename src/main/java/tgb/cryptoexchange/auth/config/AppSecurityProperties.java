package tgb.cryptoexchange.auth.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private List<String> ignoreUrls;
}
