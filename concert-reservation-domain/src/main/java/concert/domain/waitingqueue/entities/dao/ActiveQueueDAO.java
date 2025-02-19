package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.WaitingDTO;
import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActiveQueueDAO {

    private final RedissonClient redisson;

    public void migrateTokensFromWaitingQueueToActiveQueue(Collection<WaitingDTO> tokens) {
        RBatch batch = redisson.createBatch();

        for (WaitingDTO waitingDTO : tokens) {
            String uuid = waitingDTO.getUuid();
            String token = waitingDTO.getToken();
            // 활성화 큐에 추가
            batch.getMapCache(RedisKey.ACTIVE_QUEUE).putIfAbsentAsync(uuid, token, 300, TimeUnit.SECONDS);
            // 대기열에서 삭제
            batch.getMapCache(RedisKey.WAITING_QUEUE).removeAsync(token);
        }
        // 원자적으로 실행
        batch.execute();
    }

    public int getActiveQueueSize() {
        RMapCache<String, String> activeQueue = redisson.getMapCache(RedisKey.ACTIVE_QUEUE);
        return activeQueue.size();
    }


    public boolean isTokenExistsInActiveQueue(WaitingDTO waitingDTO){
        RMapCache<String, String> activeQueue = redisson.getMapCache(RedisKey.ACTIVE_QUEUE);  // RMapCache 사용
        String uuid = waitingDTO.getUuid();
        return activeQueue.containsKey(uuid);
    }

    public void deleteActiveQueueToken(String uuid) {
        RMapCache<String, String> activeQueue = redisson.getMapCache(RedisKey.ACTIVE_QUEUE);  // RMapCache 사용
        activeQueue.remove(uuid);
    }

    public void clearActiveQueue(){
        // 활성화된 대기열 비우기 (RMapCache)
        RMapCache<String, String> activeQueue = redisson.getMapCache(RedisKey.ACTIVE_QUEUE);
        activeQueue.clear();  // 활성화된 대기열의 모든 데이터 삭제
    }
}
