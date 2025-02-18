package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenHeartbeatDAO {

    private final RedissonClient redisson;

    public RMapCache<String, String> getTokenHeartbeatMap() {
        return redisson.getMapCache(RedisKey.TOKEN_HEARTBEAT);
    }
}
