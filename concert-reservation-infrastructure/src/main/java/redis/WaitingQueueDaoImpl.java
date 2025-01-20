package redis;

import concert.commons.utils.RandomStringGenerator;
import concert.domain.waitingQueue.domain.WaitingQueueDao;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMapCache;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WaitingQueueDaoImpl implements WaitingQueueDao {

    private final RedissonClient redisson;

    public WaitingQueueDaoImpl(RedissonClient redisson) {
        this.redisson = redisson;
    }

    public String addToWaitingQueue(long concertId, String uuid) {
        String timestamp = Long.toString(System.currentTimeMillis());

        String token = new StringBuilder(timestamp).append(":").append(uuid).toString();
        RSortedSet<String> waitingQueue = redisson.getSortedSet("waitingQueue:" + concertId);
        waitingQueue.add(token);

        return token;
    }

    public long getWaitingRank(long concertId, String uuid) {
        RSortedSet<String> waitingQueue = redisson.getSortedSet("waitingQueue:" + concertId);
        Collection<String> tokenList = waitingQueue.readAll();

        long rank = 1L;

        // rank 계산
        for (String token : tokenList) {
            String[] tokenInfo = token.split(":");
            if (tokenInfo[1].equals(uuid)) {
                break;
            }
            rank += 1;
        }

        // uuid가 대기열에 없음 -> -1을 반환
        if (rank > tokenList.size()) {
            return -1;
        }

        return rank;
    }

    public String getActiveQueueToken(long concertId, String uuid) {
        RMapCache<String, String> activeQueue = redisson.getMapCache("activeQueue:" + concertId);  // RMapCache 사용

        return activeQueue.get(uuid);
    }

    public void deleteActiveQueueToken(long concertId, String uuid) {
        RMapCache<String, String> activeQueue = redisson.getMapCache("activeQueue:" + concertId);  // RMapCache 사용
        activeQueue.remove(uuid);
    }

    // 각 콘서트 대기열 마다
    // 333개의 queueEntry를 1초마다 활성화열로 이동
    public void removeTop333FromWaitingQueue(long concertId) {
        RSortedSet<String> waitingQueue = redisson.getSortedSet("waitingQueue:" + concertId);
        Collection<String> tokenList = waitingQueue.readAll();

        if (tokenList.isEmpty()) {
            return;
        }

        RMapCache<String, String> activeQueue = redisson.getMapCache("activeQueue:" + concertId);  // RMapCache 사용
        Collection<String> top333 = tokenList.stream()
                .limit(333)
                .toList();

        top333.forEach(entry -> {
            String[] parts = entry.split(":");
            String uuid = parts[1];
            String token = RandomStringGenerator.generateRandomString(16);

            activeQueue.putIfAbsent(uuid, token, 300, TimeUnit.SECONDS);
        });

        top333.forEach(waitingQueue::remove);
    }
}
