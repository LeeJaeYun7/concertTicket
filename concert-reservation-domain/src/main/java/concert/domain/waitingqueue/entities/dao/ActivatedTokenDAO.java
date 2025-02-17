package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivatedTokenDAO {

    private final RedissonClient redisson;


    public boolean isActivatedTokenExists(String token){
        RSet<String> activatedTokensSet = redisson.getSet(RedisKey.ACTIVATED_TOKENS);
        return activatedTokensSet.contains(token);
    }

    public void removeActivatedToken(String token){
        RSet<String> activatedTokensSet = redisson.getSet(RedisKey.ACTIVATED_TOKENS);
        activatedTokensSet.remove(token);
    }

    public void clearActivatedTokens(){
        // 활성화된 토큰 비우기 (RSet)
        RSet<String> activatedTokensSet = redisson.getSet(RedisKey.ACTIVATED_TOKENS);
        activatedTokensSet.clear();  // 활성화된 토큰의 모든 데이터 삭제
    }
}
