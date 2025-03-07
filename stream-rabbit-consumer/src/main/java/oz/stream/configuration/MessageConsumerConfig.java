package oz.stream.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import oz.stream.model.MessageDto;
import oz.stream.service.writer.WriterService;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static oz.stream.service.writer.WriterService.FORMATER;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class MessageConsumerConfig {

    private static final List<String> MESSAGE_LIST = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService scheduledExecutorService;
    private final WriterService writerService;
    private ScheduledFuture<?> scheduledFuture;

    @Bean
    public Consumer<Message<MessageDto>> consumer() {
        return messageDtoMessage -> {
            long latencia = System.currentTimeMillis() - (long) messageDtoMessage.getHeaders().get("timestamp_ms");
            final String line = FORMATER.format(LocalTime.now()) + ";" + latencia + System.lineSeparator();
            MESSAGE_LIST.add(line);
        };
    }

    @Bean
    public CommandLineRunner schedulerWriter() {
        log.info("Scheduler writer started");
        return (noOps) -> {
            this.scheduledFuture = this.scheduledExecutorService.scheduleWithFixedDelay(() -> {

                if (MESSAGE_LIST.size() > 1) {
                    writerService.writer(String.join("", MESSAGE_LIST));
                    log.info("Latencia has sido escrita correctamente {}, lines", MESSAGE_LIST.size());
                    this.scheduledFuture.cancel(true);
                    log.info("Scheduler writer isCancelled ? {}", this.scheduledFuture.isCancelled());
                }

            }, 0, 12, TimeUnit.MINUTES);
        };
    }

}
