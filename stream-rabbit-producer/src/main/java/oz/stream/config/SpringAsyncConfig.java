package oz.stream.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
        //executor.setQueueCapacity(20);  // Queue capacity for pending tasks
        executor.setThreadNamePrefix("ProducerTaskExecutor-");  // Prefix for thread names
        //executor.setWaitForTasksToCompleteOnShutdown(true);  // Ensures tasks complete on shutdown
        //executor.setAwaitTerminationSeconds(60);  // Timeout for waiting for tasks to complete
        executor.initialize();  // Initializes the thread pool
        return executor;
    }
}