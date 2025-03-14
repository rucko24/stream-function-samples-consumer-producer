package oz.stream.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import oz.stream.config.producerglobalconfig.ProducerGlobalConfigPropertySourceFactory;
import oz.stream.service.SendMessageService;
import oz.stream.service.SendRandomMessageService;

@Log4j2
@Configuration
@PropertySource(value = "file:./global-configuration.yml", factory = ProducerGlobalConfigPropertySourceFactory.class)
public class RunnerConfigurationLocal {

    @Bean(name = "runMessages")
    @ConditionalOnProperty(name = "producer.enable-random-messages", havingValue = "false")
    public CommandLineRunner runMessages(SendMessageService sendMessageService) {
        return (args) -> {
            log.info("Local Random messages enabled: false");
            sendMessageService.producer("Enviando mensaje de prueba");
        };
    }

    @Bean(name = "runRandomMessages")
    @ConditionalOnProperty(name = "producer.enable-random-messages", havingValue = "true")
    public CommandLineRunner runRandomMessages(SendRandomMessageService sendRandomMessageService) {
        return (args) -> {
            log.info("Local Random messages enabled: true");
            sendRandomMessageService.producer("Enviando mensaje de prueba");
        };
    }

}
