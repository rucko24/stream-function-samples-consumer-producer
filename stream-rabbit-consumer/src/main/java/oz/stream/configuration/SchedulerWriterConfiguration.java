package oz.stream.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
@ConfigurationProperties(prefix = "scheduler.latency-writing")
public class SchedulerWriterConfiguration {

    private static final AtomicLong COUNTER = new AtomicLong();

    private Integer delayInMinutes;

    public void setDelayInMinutes(Integer delayInMinutes) {
        this.delayInMinutes = delayInMinutes;
    }

    public Integer getDelayInMinutes() {
        return delayInMinutes;
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public ScheduledExecutorService scheduledExecutorService() {
        final ThreadFactory threadFactory = runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("SchedulerWriter-" + COUNTER.incrementAndGet());
            return thread;
        };
        return Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

}