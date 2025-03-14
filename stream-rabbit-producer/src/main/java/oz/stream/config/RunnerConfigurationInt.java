package oz.stream.config;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import oz.stream.config.producerglobalconfig.ProducerGlobalConfigPropertySourceFactory;
import oz.stream.service.SendMessageService;
import oz.stream.service.SendRandomMessageService;

@Data
@Log4j2
@Profile("!LOCAL")
@Configuration
@PropertySource(value = "file:/home/rubn/global-configuration-dev.yml", factory = ProducerGlobalConfigPropertySourceFactory.class)
public class RunnerConfigurationInt {

    private Integer corePoolSize;

    @Bean
    @ConditionalOnProperty(name = "producer.enable-random-messages", havingValue = "false")
    public CommandLineRunner intRunMessages(SendMessageService sendMessageService) {
        return (args) -> {
            log.info("Int Random messages enabled: false corePoolSize {}", corePoolSize);
            sendMessageService.producer("Enviando mensaje de prueba");
        };
    }

    @Bean
    @ConditionalOnProperty(name = "producer.enable-random-messages", havingValue = "true")
    public CommandLineRunner intRunRandomMessages(SendRandomMessageService sendRandomMessageService) {
        return (args) -> {
            log.info("Int Random messages enabled: true corePoolSize {}", corePoolSize);
            sendRandomMessageService.producer("Enviando mensaje de prueba");
        };
    }

}
