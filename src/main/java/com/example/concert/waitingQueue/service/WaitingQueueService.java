package com.example.concert.waitingQueue.service;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.common.Loggable;
import com.example.concert.utils.TimeProvider;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.domain.WaitingQueueStatus;
import com.example.concert.waitingQueue.repository.WaitingQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WaitingQueueService {

    private final TimeProvider timeProvider;
    private final WaitingQueueRepository waitingQueueRepository;

    public WaitingQueueService(TimeProvider timeProvider, WaitingQueueRepository waitingQueueRepository){
        this.timeProvider = timeProvider;
        this.waitingQueueRepository = waitingQueueRepository;
    }

    public long getWaitingNumber(String token) {
        WaitingQueue element = waitingQueueRepository.findByToken(token)
                                                     .orElseThrow(() -> new CustomException(ErrorCode.WAITING_QUEUE_NOT_FOUND, Loggable.NEVER));

        return element.getWaitingNumber();
    }

    @Transactional
    public List<WaitingQueue> getAllByConcertId(long concertId){
        return waitingQueueRepository.findAllByConcertIdWithLock(concertId);
    }

    public void save(WaitingQueue newElement){
        waitingQueueRepository.save(newElement);
    }


    public Optional<WaitingQueue> getByUuid(String uuid){
        return waitingQueueRepository.findByUuid(uuid);
    }

    public void delete(long concertId, String uuid){

        // concertId와 uuid로 토큰을 검색
        WaitingQueue token = waitingQueueRepository.findByConcert_IdAndUuid(concertId, uuid).get();

        // 해당 토큰의 대기 순서 확인
        long waitingNumber = token.getWaitingNumber();

        // 해당 토큰 삭제
        waitingQueueRepository.deleteByConcert_IdAndUuid(concertId, uuid);

        // 대기 순서 업데이트
        updateWaitingNumber(concertId, waitingNumber);
    }

    public void updateWaitingNumber(long concertId, long waitingNumber){
        List<WaitingQueue> tokens = waitingQueueRepository.findAllByConcertIdWithLock(concertId);
        for(WaitingQueue token: tokens){
            if(token.getWaitingNumber() > waitingNumber){
                token.updateWaitingNumber();
            }
        }
    }

    // 대기열에서 빠져 나와서 활성화된 토큰은 WaitingNumber가 0으로 관리됨
    public void processNextCustomer(long concertId) {
        Optional<WaitingQueue> activeTokenOpt = waitingQueueRepository.findByConcertIdAndWaitingNumber(concertId, 0);

        // 고객 1명이 아직 결제를 끝마치지 않은 상황
        if(activeTokenOpt.isPresent()){
            WaitingQueue activeToken = activeTokenOpt.get();

            // 해당 고객의 시각이 활성화된지 10분을 넘지 않았다면,
            if(!isTenMinutesPassed(activeToken.getUpdatedAt())){
                return;
            }
            activeToken.updateWaitingNumber();
            activeToken.updateWaitingQueueStatus(WaitingQueueStatus.DONE);
        }

        WaitingQueue firstToken = waitingQueueRepository.findByConcertIdAndWaitingNumber(concertId, 1)
                                                        .orElseThrow(() -> new CustomException(ErrorCode.WAITING_QUEUE_NOT_FOUND, Loggable.NEVER));
        LocalDateTime now = timeProvider.now();
        firstToken.activateToken(now);
        List<WaitingQueue> tokens = waitingQueueRepository.findAllByConcertIdWithLock(concertId);

        for(WaitingQueue token: tokens){
            token.updateWaitingNumber();
        }
    }

    public void updateWaitingQueueStatus(String token) {
        WaitingQueue expiredToken = waitingQueueRepository.findByToken(token)
                                                          .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND, Loggable.NEVER));

        expiredToken.updateWaitingQueueStatus(WaitingQueueStatus.DONE);
        expiredToken.updateWaitingNumber();
    }

    private boolean isTenMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = timeProvider.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 10;
    }
}
