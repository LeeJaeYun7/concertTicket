package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenSessionDAO {

    private final RedissonClient redisson;

    public boolean isTokenSessionExists(String token){
        RMap<String, String> sessionsMap = redisson.getMap(RedisKey.TOKEN_SESSION_ID);
        return sessionsMap.containsKey(token);
    }

    public void removeTokenSession(String token){
        RMap<String, String> sessionsMap = redisson.getMap(RedisKey.TOKEN_SESSION_ID);
        sessionsMap.remove(token);  // 해당 토큰에 대한 세션 정보 삭제
    }

}
