package com.example.concert.interceptor.fixtures;

import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.domain.WaitingQueueStatus;

import java.time.LocalDateTime;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class WaitingQueueFixtureFactory {

    public static WaitingQueue createWaitingQueue(){
        return new WaitingQueue();
    }

    public static WaitingQueue createWaitingQueueWithTokenCreated5MinutesAgo(String token){
        WaitingQueue waitingQueue = createWaitingQueue();
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        setField(waitingQueue, "token", token);
        setField(waitingQueue, "status", WaitingQueueStatus.ACTIVE);
        setField(waitingQueue, "updatedAt", fiveMinutesAgo);
        return waitingQueue;
    }

    public static WaitingQueue createWaitingQueueWithTokenCreated15MinutesAgo(String token){
        WaitingQueue waitingQueue = createWaitingQueue();
        LocalDateTime fifteenMinutesAgo = LocalDateTime.now().minusMinutes(15);

        setField(waitingQueue, "token", token);
        setField(waitingQueue, "status", WaitingQueueStatus.ACTIVE);
        setField(waitingQueue, "updatedAt", fifteenMinutesAgo);
        return waitingQueue;
    }

    public static WaitingQueue createWaitingQueueWithWaitingToken(String token){
        WaitingQueue waitingQueue = createWaitingQueue();
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        setField(waitingQueue, "token", token);
        setField(waitingQueue, "status", WaitingQueueStatus.WAITING);
        setField(waitingQueue, "updatedAt", fiveMinutesAgo);
        return waitingQueue;
    }
}