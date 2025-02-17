package concert.domain.waitingqueue.entities.enums;

public interface RedisKey {
    String WAITING_QUEUE = "waitingQueue";
    String ACTIVE_QUEUE = "activeQueue";
    String ACTIVATED_TOKENS = "activatedTokens";
    String TOKEN_SESSION_ID = "tokenSessionId";
    String WAITING_QUEUE_STATUS = "waitingQueueStatusKey";
    String WAITING_QUEUE_STATUS_ACTIVE = "active";
    String ACTIVE_QUEUE_LOCK = "activeQueueLock";
    String TOKEN_PUB_SUB_CHANNEL = "tokenChannel";
    String WAITING_QUEUE_STATUS_PUB_SUB_CHANNEL = "waitingQueueStatusChannel";
    String WAITING_QUEUE_STATUS_KEY = "waitingQueueStatusKey";
    String TOKEN_HEARTBEAT_KEY = "userHeartbeat";
}
