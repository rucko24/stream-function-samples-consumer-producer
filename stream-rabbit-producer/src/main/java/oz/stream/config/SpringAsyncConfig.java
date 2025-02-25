package oz.stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class SpringAsyncConfig {

    private AtomicInteger THREAD_COUNTER = new AtomicInteger();

    @Bean
    public TaskExecutor threadPoolTaskExecutor(AppConfiguration configuration) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(configuration.getCorePoolSize());  // Minimum number of threads in the pool
        executor.setMaxPoolSize(configuration.getMaxCorePoolSize());  // Maximum number of threads in the pool
        executor.setThreadNamePrefix("RabbitProducerTaskExecutor-");  // Prefix for thread names
        executor.initialize();  // Initializes the thread pool
        return executor;
    }

    //@Bean
    public ExecutorService threadPoolTaskExecutor2(AppConfiguration configuration) {

        final ThreadFactory threadFactory = runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("RabbitProducerExecutor-" + THREAD_COUNTER.incrementAndGet());
            return thread;
        };

        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                configuration.getCorePoolSize(),
                configuration.getCorePoolSize(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );

        executor.setThreadFactory(threadFactory);
        executor.setCorePoolSize(configuration.getCorePoolSize());
        executor.prestartAllCoreThreads();

        return executor;
    }

}