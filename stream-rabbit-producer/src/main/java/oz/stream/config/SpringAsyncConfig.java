package oz.stream.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class SpringAsyncConfig {

    @Bean
    public TaskExecutor threadPoolTaskExecutor(AppConfiguration configuration) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(configuration.getCorePoolSize());  // Minimum number of threads in the pool
        executor.setMaxPoolSize(configuration.getMaxCorePoolSize());  // Maximum number of threads in the pool
        executor.setThreadNamePrefix("RabbitProducerTaskExecutor-");  // Prefix for thread names
        executor.initialize();  // Initializes the thread pool
        return executor;
    }
}