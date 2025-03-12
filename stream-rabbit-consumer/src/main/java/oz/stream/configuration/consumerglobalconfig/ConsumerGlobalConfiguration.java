package oz.stream.configuration.consumerglobalconfig;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Log4j2
@Data
@Configuration
@PropertySource(value = "file:./global-configuration.yml", factory = ConsumerGlobalConfigPropertySourceFactory.class)
@ConfigurationProperties(prefix = "consumer")
public class ConsumerGlobalConfiguration {
    // Definición de propiedades, getters y setters
    private Integer concurrency;

    @PostConstruct
    public void setup() {
        System.setProperty("spring.cloud.stream.bindings.consumer-in-0.destination", "performance-queue");
        System.setProperty("spring.cloud.stream.bindings.consumer-in-0.group", "my-consumer-group");
        System.setProperty("spring.cloud.stream.bindings.consumer-in-0.consumer.concurrency", String.valueOf(concurrency));
    }

    @Bean
    public CommandLineRunner runner(){
        return args -> {
            log.info("core pool size {}", concurrency);
        };
    }


}
