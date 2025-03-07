package oz.stream.configuration.consumerglobalconfig;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Log4j2
@Data
@Configuration
@PropertySource(value = "file:/home/rubn/global-configuration.yml", factory = ConsumerGlobalConfigPropertySourceFactory.class)
@ConfigurationProperties(prefix = "global-config.consumer")
public class ConsumerGlobalConfiguration {
    // Definición de propiedades, getters y setters
    private Integer corePoolSize;

    @Bean
    public CommandLineRunner runner(){
        return args -> {
            log.info("core pool size {}", corePoolSize);
        };
    }

}
