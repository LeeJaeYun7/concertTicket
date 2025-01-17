package waitingQueue.application.scheduler;

import common.CustomException;
import concert.application.ConcertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.WaitingQueueDao;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WaitingQueueScheduler {

    private final ConcertService concertService;
    private final WaitingQueueDao waitingQueueDao;

    @Scheduled(fixedRate = 100000)
    public void processWaitingQueue() {
        List<Long> concertIds = concertService.getAllConcertIds();

        for (long concertId: concertIds){
            try{
                waitingQueueDao.removeTop333FromWaitingQueue(concertId);
            }catch(CustomException e){
                log.error("대기열을 처리하는 중 에러 발생: " + e.getMessage());
            }
        }
    }
}
