package oz.stream.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import oz.stream.model.MessageDto;
import oz.stream.service.writer.WriterService;

import java.util.function.Consumer;

@Log4j2
@Configuration
public class MessageConsumerConfig {

    @Bean
    public Consumer<Message<MessageDto>> consumer(WriterService writerService) {
        return messageDtoMessage -> {
            long latencia = System.currentTimeMillis() - (long) messageDtoMessage.getHeaders().get("timestamp_ms");
            //log.info("Mensaje recibido (ms) {} thread: {}", latencia, Thread.currentThread().getName());
            writerService.writer(latencia);
        };
    }

}
