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
public class WaitingQueueStatusDAO {

    private final RedissonClient redisson;

    public String getWaitingQueueStatus() {
        RMap<String, String> waitingQueueStatusMap = redisson.getMap(RedisKey.WAITING_QUEUE_STATUS);
        return waitingQueueStatusMap.getOrDefault("status", "inactive");
    }

    public String getWaitingQueueStatusLastChanged() {
        RMap<String, String> waitingQueueStatusMap = redisson.getMap(RedisKey.WAITING_QUEUE_STATUS);
        return waitingQueueStatusMap.getOrDefault("lastChanged", null);
    }

    public void changeWaitingQueueStatus(String status, long now) {
        RMap<String, String> waitingQueueStatusMap = redisson.getMap(RedisKey.WAITING_QUEUE_STATUS);
        waitingQueueStatusMap.put("status", status);
        waitingQueueStatusMap.put("lastChanged", String.valueOf(now));
    }
}
