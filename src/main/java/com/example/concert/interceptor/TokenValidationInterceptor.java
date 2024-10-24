package com.example.concert.interceptor;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.common.Loggable;
import com.example.concert.utils.TimeProvider;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.domain.WaitingQueueStatus;
import com.example.concert.waitingQueue.repository.WaitingQueueRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Autowired
    private TimeProvider timeProvider;

    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || !validateToken(token)) {
            throw new CustomException(ErrorCode.NOT_VALID_TOKEN, Loggable.ALWAYS);
        }

        return true;
    }

    private boolean validateToken(String token) {
        Optional<WaitingQueue> waitingQueueOpt = waitingQueueRepository.findByToken(token);

        return waitingQueueOpt.get().getStatus().equals(WaitingQueueStatus.ACTIVE)
                && !isTenMinutesPassed(waitingQueueOpt.get().getUpdatedAt());
    }

    private boolean isTenMinutesPassed(LocalDateTime updatedAt) {
        LocalDateTime now = timeProvider.now();
        Duration duration = Duration.between(updatedAt, now);
        return duration.toMinutes() >= 10;
    }
}
