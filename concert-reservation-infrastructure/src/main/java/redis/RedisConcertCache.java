package redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import concert.domain.concert.cache.ConcertCache;
import concert.domain.concert.domain.Concert;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

@Primary
@Component
@RequiredArgsConstructor
public class RedisConcertCache implements ConcertCache {

    private final RedissonClient redisson;

    private static final Logger log = (Logger) LoggerFactory.getLogger(RedisConcertCache.class);
    private static final String TOP30_CONCERTS = "top30concerts";
    private final ObjectMapper objectMapper;

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackSaveConcerts")
    public void saveTop30Concerts(List<Concert> concerts) throws JsonProcessingException {
        RMap<String, String> top30concertMap = redisson.getMap(TOP30_CONCERTS);
        String top30concertListJson = objectMapper.writeValueAsString(concerts);

        top30concertMap.put("top30concerts", top30concertListJson);
        top30concertMap.expire(Duration.ofMinutes(5).plusSeconds(10));

        log.info("Top 30 concerts saved to Redis as JSON.");
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackSaveConcerts")
    public List<Concert> findTop30Concerts() throws JsonProcessingException {
        RMap<String, String> top30concertMap = redisson.getMap(TOP30_CONCERTS);

        String top30concertListJson = top30concertMap.get("top30concerts");

        if (top30concertListJson != null && !top30concertListJson.isEmpty()) {
            log.info("Retrieved top 30 concerts from Redis.");
            return objectMapper.readValue(top30concertListJson, objectMapper.getTypeFactory().constructCollectionType(List.class, Concert.class));
        } else {
            log.warning("No top 30 concerts found in Redis.");
            return null;
        }
    }

    // Fallback 메소드 정의 (서킷 브레이커가 열렸을 때 호출됨)
    public void fallbackSaveConcerts(String concerts, Throwable t) {
        log.severe("Failed to save concerts to Redis. Circuit breaker is open.");
    }

    public String fallbackGetConcerts(Throwable t) {
        log.severe("Failed to retrieve concerts from Redis. Circuit breaker is open.");
        return null;
    }
}
