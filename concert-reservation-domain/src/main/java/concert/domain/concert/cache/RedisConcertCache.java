package concert.domain.concert.cache;

import concert.domain.concert.entities.ConcertEntity;
import concert.domain.shared.utils.DomainJsonConverter;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Primary
@Component
@RequiredArgsConstructor
public class RedisConcertCache implements ConcertCache {

    private final DomainJsonConverter domainJsonConverter;
    private final RedissonClient redisson;
    private static final String TOP30_CONCERTS = "top30concerts";

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackSaveConcerts")
        public void saveTop30Concerts(List<ConcertEntity> concerts){
        RMap<String, String> top30concertMap = redisson.getMap(TOP30_CONCERTS);
        String top30concertListJson = domainJsonConverter.convertToJson(concerts);

        top30concertMap.put("top30concerts", top30concertListJson);
        top30concertMap.expire(Duration.ofMinutes(5).plusSeconds(10));

        log.info("Top 30 concerts saved to Redis as JSON.");
    }

    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "fallbackSaveConcerts")
    public List<ConcertEntity> findTop30Concerts() {
        RMap<String, String> top30concertMap = redisson.getMap(TOP30_CONCERTS);

        String top30concertListJson = top30concertMap.get("top30concerts");

        if (top30concertListJson != null && !top30concertListJson.isEmpty()) {
            log.info("Retrieved top 30 concerts from Redis.");
            return domainJsonConverter.convertFromJsonToList(top30concertListJson, ConcertEntity.class);
        } else {
            log.warn("No top 30 concerts found in Redis.");
            return null;
        }
    }

    // Fallback 메소드 정의 (서킷 브레이커가 열렸을 때 호출됨)
    public void fallbackSaveConcerts(String concerts, Throwable t) {
        log.error("Failed to save concerts to Redis. Circuit breaker is open.");
    }

    public String fallbackGetConcerts(Throwable t) {
        log.error("Failed to retrieve concerts from Redis. Circuit breaker is open.");
        return null;
    }
}
