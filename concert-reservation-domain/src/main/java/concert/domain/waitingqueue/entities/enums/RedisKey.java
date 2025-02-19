package concert.domain.waitingqueue.entities.enums;

public interface RedisKey {
    String WAITING_QUEUE = "waitingQueue";
    String ACTIVE_QUEUE = "activeQueue";
    String WAITING_QUEUE_STATUS_ACTIVE = "active";
    String ACTIVE_QUEUE_LOCK = "activeQueueLock";
    String ACTIVE_TOKEN_PUB_SUB_CHANNEL = "activeTokenChannel";
    String WAITING_TOKEN_PUB_SUB_CHANNEL = "waitingTokenChannel";

    String TOKEN_REMOVAL_PUB_SUB_CHANNEL = "tokenRemovalChannel";
    String WAITING_QUEUE_STATUS_PUB_SUB_CHANNEL = "waitingQueueStatusChannel";
    String WAITING_QUEUE_STATUS = "waitingQueueStatus";
    String TOKEN_HEARTBEAT= "userHeartbeat";
}
