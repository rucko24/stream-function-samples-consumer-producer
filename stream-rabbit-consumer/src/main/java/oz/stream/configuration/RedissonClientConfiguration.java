package oz.stream.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RedissonClientConfiguration {

    //@Bean
    public RedissonClient redissonClient(RedissonCredentialsConfiguration credentialsConfiguration) {
        Config config = new Config();
        // Suponiendo que Redis corre localmente en el puerto 6379
        config.useSingleServer().setAddress(credentialsConfiguration.getUrl());
        config.useSingleServer().setPassword(credentialsConfiguration.getPassword());
        return Redisson.create(config);
    }

}
