package concert.infrastructure.distributedlock.dao;

import concert.infrastructure.distributedlock.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenConcertScheduleSeatDAO {

    private final RedissonClient redisson;
    private static final long ttl = 300L;

    public void saveTokenScheduleSeat(String concertScheduleSeatId, String token) {
        RMapCache<String, String> tokenScheduleSeat = redisson.getMapCache(RedisKey.TOKEN_CONCERTSCHEDULE_SEAT);

        // 저장 직전 로그 추가
        log.info("Saving to Redis: key=" + concertScheduleSeatId + ", token=" + token);
        tokenScheduleSeat.put(concertScheduleSeatId, token, ttl, TimeUnit.SECONDS);
    }

    public boolean isExistsToken(String concertScheduleSeatId, String token) {
        RMapCache<String, String> tokenScheduleSeat = redisson.getMapCache(RedisKey.TOKEN_CONCERTSCHEDULE_SEAT);
        String storedToken = tokenScheduleSeat.get(concertScheduleSeatId);

        // 불러온 값 로그 출력
        log.info("Checking token in Redis: key={}, storedToken={}", concertScheduleSeatId, storedToken);

        return token.equals(storedToken);
    }

    public List<String> findConcertScheduleSeatIdsByToken(String token) {
        RMapCache<String, String> tokenScheduleSeat = redisson.getMapCache(RedisKey.TOKEN_CONCERTSCHEDULE_SEAT);

        List<String> concertScheduleSeatIds = new ArrayList<>();

        for (String concertScheduleSeatId : tokenScheduleSeat.keySet()) {
            if (token.equals(tokenScheduleSeat.get(concertScheduleSeatId))) {
                concertScheduleSeatIds.add(concertScheduleSeatId); // 해당 token이 매핑된 concertScheduleSeatId 반환
            }
        }
        return concertScheduleSeatIds; // 없으면 null 반환
    }

    public void removeTokenScheduleSeat(String concertScheduleSeatId) {
        RMapCache<String, String> tokenScheduleSeat = redisson.getMapCache(RedisKey.TOKEN_CONCERTSCHEDULE_SEAT);
        tokenScheduleSeat.remove(concertScheduleSeatId);
        log.info("Removed token mapping for concertScheduleSeatId: {}", concertScheduleSeatId);
    }
}
