package concert.domain.waitingqueue.entities.dao;

import concert.domain.waitingqueue.entities.enums.RedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaitingQueueStatusPublisher {

    private final RedissonClient redisson;

    public void publishWaitingQueueStatus(String status) {
        RTopic topic = redisson.getTopic(RedisKey.WAITING_QUEUE_STATUS_PUB_SUB_CHANNEL);
        topic.publish(status);
    }
}
