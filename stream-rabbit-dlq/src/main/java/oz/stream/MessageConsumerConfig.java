package oz.stream;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Log4j2
@Configuration// Habilita el binding de entrada (Sink)
public class MessageConsumerConfig {

    @Bean
    public Consumer<String> consumer() {
        return message -> {
            log.info("Mensaje recibido: {} thread: {}", message, Thread.currentThread().getName());
        };
    }

}
