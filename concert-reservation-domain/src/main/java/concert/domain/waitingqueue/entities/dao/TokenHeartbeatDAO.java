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

    public boolean isTokenHeartbeatExists(String token){
        RMap<String, String> tokenHeartbeatMap = redisson.getMap(RedisKey.TOKEN_HEARTBEAT_KEY);
        return tokenHeartbeatMap.containsKey(token);
    }

    public RMapCache<String, String> getTokenHeartbeat() {
        return redisson.getMapCache(RedisKey.TOKEN_HEARTBEAT_KEY);
    }

    public void removeTokenHeartbeat(String token) {
        RMap<String, String> tokenHeartbeatMap = redisson.getMap(RedisKey.TOKEN_HEARTBEAT_KEY);
        tokenHeartbeatMap.remove(token);
    }
}
