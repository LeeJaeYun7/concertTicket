package waitingQueue.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.WaitingQueueDao;
import waitingQueue.application.dto.WaitingRankResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitingQueueService {

    private final WaitingQueueDao waitingQueueDao;

    public String retrieveToken(long concertId, String uuid) {
        return waitingQueueDao.addToWaitingQueue(concertId, uuid);
    }

    public WaitingRankResponse retrieveWaitingRank(long concertId, String uuid) {
        long rank = waitingQueueDao.getWaitingRank(concertId, uuid);

        if(rank == -1){
            return WaitingRankResponse.of(rank, "active");
        }

        return WaitingRankResponse.of(rank, "waiting");
    }
}
