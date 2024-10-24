package com.example.concert.waitingQueue.service;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.common.Loggable;
import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.service.ConcertService;
import com.example.concert.utils.RandomStringGenerator;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.vo.TokenVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.max;

@Service
public class WaitingQueueFacade {

    private final ConcertService concertService;
    private final WaitingQueueService waitingQueueService;

    public WaitingQueueFacade(ConcertService concertService, WaitingQueueService waitingQueueService){
        this.concertService = concertService;
        this.waitingQueueService = waitingQueueService;
    }

    @Transactional
    public TokenVO createToken(long concertId, UUID uuid) {

        checkQueueExists(concertId, uuid);

        Concert concert = concertService.getConcertById(concertId);

        String newToken = RandomStringGenerator.generateRandomString(16);

        List<WaitingQueue> tokenList = waitingQueueService.getAllByConcertId(concertId);

        long end = 0;

        for(WaitingQueue token: tokenList){
            end = max(end, token.getWaitingNumber());
        }

        WaitingQueue newWaitingQueue = WaitingQueue.of(concert, uuid, newToken, end+1);
        waitingQueueService.save(newWaitingQueue);

        return TokenVO.of(newToken, end+1);
    }

    private void checkQueueExists(long concertId, UUID uuid) {
        Optional<WaitingQueue> tokenOpt = waitingQueueService.getByUuid(uuid);

        // 대기열에 uuid로 만든 토큰이 없는 경우
        if(tokenOpt.isEmpty()){
            return;
        }

        WaitingQueue token = tokenOpt.get();

        // 같은 대기열에 uuid로 만든 토큰이 존재하는 경우
        if(token.getConcert().getId() == concertId){
            throw new CustomException(ErrorCode.TOKEN_ALREADY_EXISTS, Loggable.NEVER);
        }

        // 다른 대기열에 uuid로 만든 토큰이 존재하는 경우, 삭제해준다
        waitingQueueService.delete(token.getConcert().getId(), uuid);
    }
}
