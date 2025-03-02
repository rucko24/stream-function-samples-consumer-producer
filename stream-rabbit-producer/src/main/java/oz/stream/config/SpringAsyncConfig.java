package oz.stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class SpringAsyncConfig {

    private AtomicInteger THREAD_COUNTER = new AtomicInteger();

    @Bean
    public ScheduledExecutorService scheduledExecutorService(AppConfiguration configuration) {

        final ThreadFactory threadFactory = runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("RabbitProducerExecutor-" + THREAD_COUNTER.incrementAndGet());
            return thread;
        };

        return Executors.newScheduledThreadPool(configuration.getCorePoolSize(), threadFactory);
    }

}