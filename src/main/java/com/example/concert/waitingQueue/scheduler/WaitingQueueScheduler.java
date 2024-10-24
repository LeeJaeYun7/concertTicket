package com.example.concert.waitingQueue.scheduler;

import com.example.concert.common.CustomException;
import com.example.concert.concert.service.ConcertService;
import com.example.concert.waitingQueue.service.WaitingQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class WaitingQueueScheduler {

    private final ConcertService concertService;
    private final WaitingQueueService waitingQueueService;

    public WaitingQueueScheduler(ConcertService concertService, WaitingQueueService waitingQueueService){
        this.concertService = concertService;
        this.waitingQueueService = waitingQueueService;
    }

    @Scheduled(fixedRate = 3000)
    @Transactional
    public void processWaitingQueue() {

        List<Long> concertIds = concertService.getAllConcertIds();

        for (long concertId: concertIds){
            try{
                waitingQueueService.processNextCustomer(concertId);
            }catch(CustomException e){
                log.error("대기열을 처리하는 중 에러 발생: " + e.getMessage());
            }
        }
    }
}
