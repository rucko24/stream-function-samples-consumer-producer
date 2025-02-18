package oz.stream.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "redis-credentials")
public class RedissonCredentialsConfiguration {

    private String url;
    private String password;

}
