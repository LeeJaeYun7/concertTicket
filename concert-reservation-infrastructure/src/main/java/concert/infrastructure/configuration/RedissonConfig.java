package concert.infrastructure.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedissonConfig {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    private RedissonClient redisson;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);
        config.setCodec(new StringCodec());
        redisson = Redisson.create(config);

        try {
            redisson.getBucket("testKey").get();
            log.info("Successfully connected to Redis server at {}:{}", redisHost, redisPort);
        } catch (Exception e) {
            log.error("Failed to connect to Redis server at {}:{}", redisHost, redisPort, e);
        }

        return redisson;
    }
}
