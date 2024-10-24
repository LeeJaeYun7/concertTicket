package com.example.concert.filter.fixtures;

import com.example.concert.waitingQueue.domain.WaitingQueue;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class WaitingQueueFixtureFactory {

    public static WaitingQueue createWaitingQueue(){
        return new WaitingQueue();
    }

    public static WaitingQueue createWaitingQueueWithToken(String token){
        WaitingQueue waitingQueue = createWaitingQueue();

        setField(waitingQueue, "token", token);
        return waitingQueue;
    }
}
