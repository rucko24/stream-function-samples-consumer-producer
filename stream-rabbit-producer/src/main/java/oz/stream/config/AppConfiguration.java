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
    private String configFile;
    private String messageFile;

    public String getConfigFile() {
        return "/configuration_" + configFile + ".json";
    }

    public String getMessageFile() {
        return "/" + configFile + ".json";
    }
}
