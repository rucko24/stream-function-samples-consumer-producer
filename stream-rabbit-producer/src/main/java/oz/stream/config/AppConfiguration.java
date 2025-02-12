package oz.stream.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.core")
public class AppConfiguration {

    private Integer corePoolSize;
    private Integer maxCorePoolSize;
    private Integer replicasOrInstances;

}
