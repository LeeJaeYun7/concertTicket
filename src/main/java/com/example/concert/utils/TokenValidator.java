package com.example.concert.utils;

import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.domain.WaitingQueueStatus;
import com.example.concert.waitingQueue.repository.WaitingQueueRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TokenValidator {

    private final TimeProvider timeProvider;
    private final WaitingQueueRepository waitingQueueRepository;

    public TokenValidator(TimeProvider timeProvider, WaitingQueueRepository waitingQueueRepository){
        this.timeProvider = timeProvider;
        this.waitingQueueRepository = waitingQueueRepository;
    }
    public boolean validateToken(String token){
        Optional<WaitingQueue> waitingQueueOpt = waitingQueueRepository.findByToken(token);
        return waitingQueueOpt.isPresent() && waitingQueueOpt.get().getStatus().equals(WaitingQueueStatus.ACTIVE)
                && !isTenMinutesPassed(waitingQueueOpt.get().getUpdatedAt());
    }

    private boolean isTenMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = timeProvider.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 10;
    }
}
