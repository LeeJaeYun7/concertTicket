package concert.infrastructure.monitoring;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
public class TrafficMonitoringFilter implements Filter {

    private final RedissonClient redissonClient;

    public TrafficMonitoringFilter(RedissonClient redissonClient) {

        this.redissonClient = redissonClient;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            String apiName = httpServletRequest.getRequestURI();
            incrementTraffic(apiName);
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 슬라이딩 윈도우 방식으로 최근 1분간 트래픽을 확인
    public long getTrafficForLastMinute(String apiName) {
        long currentSecond = Instant.now().getEpochSecond(); // 현재 초 단위로 시간 구하기

        // 트래픽 데이터를 저장하는 RMap
        RMap<String, Long> trafficMap = redissonClient.getMap("traffic:" + apiName, LongCodec.INSTANCE);

        long totalTraffic = 0;

        // 최근 1분의 데이터를 계산
        for (long i = currentSecond - 60; i < currentSecond; i++) {
            String key = "second:" + i;
            totalTraffic += trafficMap.getOrDefault(key, 0L);
        }

        return totalTraffic;
    }

    // 트래픽을 증가시키는 메서드
    public void incrementTraffic(String apiName) {
        long currentSecond = Instant.now().getEpochSecond(); // 현재 초 단위로 시간 구하기

        // 트래픽 데이터를 저장하는 RMap
        RMap<String, Long> trafficMap = redissonClient.getMap("traffic:" + apiName, LongCodec.INSTANCE);

        String key = "second:" + currentSecond; // 초 단위 키
        trafficMap.addAndGet(key, 1L); // 내부적으로 Redis HINCRBY 실행
    }
}