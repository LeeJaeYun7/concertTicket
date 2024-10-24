package com.example.concert.waitingQueue;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.utils.RandomStringGenerator;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.domain.WaitingQueueStatus;
import com.example.concert.waitingQueue.repository.WaitingQueueRepository;
import com.example.concert.waitingQueue.service.WaitingQueueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class WaitingIntegrationTest {

    @Autowired
    private WaitingQueueService sut;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private WaitingQueueRepository waitingQueueRepository;

    @Nested
    @DisplayName("토큰을 삭제할 때")
    class 토큰을_삭제할때 {

        @Test
        @DisplayName("concertId와 UUID가 전달될 때, 토큰이 삭제된다")
        void concertId와_UUID가_전달될때_토큰이_삭제된다() {
            Concert concert = Concert.of("박효신 콘서트");
            Concert savedConcert = concertRepository.save(concert);

            String uuid = UUID.randomUUID().toString();
            String token = RandomStringGenerator.generateRandomString(16);
            WaitingQueue foundToken = WaitingQueue.of(savedConcert, uuid, token, 1);
            waitingQueueRepository.save(foundToken);

            sut.delete(savedConcert.getId(), uuid);
        }
    }

    @Nested
    @DisplayName("대기 번호를 업데이트할 때")
    class 대기_번호를_업데이트할때 {

        @Test
        @DisplayName("concertId와 waitingNumber가 전달될 때, 전달된 waitingNumber보다 큰 waitingNumber는 값이 1 감소한다")
        void concertId와_waitingNumber가_전달될때_전달된_waitingNumber보다_큰_waitingNumber는_값이_1_감소한다() {
            Concert concert = Concert.of("박효신 콘서트");
            Concert savedConcert = concertRepository.save(concert);

            String uuid = UUID.randomUUID().toString();
            String token = RandomStringGenerator.generateRandomString(16);

            WaitingQueue foundToken1  = WaitingQueue.of(savedConcert, uuid, token, 1);
            WaitingQueue foundToken2  = WaitingQueue.of(savedConcert, uuid, token, 2);
            WaitingQueue foundToken3  = WaitingQueue.of(savedConcert, uuid, token, 3);
            WaitingQueue foundToken4  = WaitingQueue.of(savedConcert, uuid, token, 4);

            waitingQueueRepository.save(foundToken1);
            waitingQueueRepository.save(foundToken2);
            waitingQueueRepository.save(foundToken3);
            waitingQueueRepository.save(foundToken4);

            sut.updateWaitingNumber(savedConcert.getId(), 2);
            assertEquals(2, foundToken3.getWaitingNumber());
            assertEquals(3, foundToken4.getWaitingNumber());
        }
    }

    @Nested
    @DisplayName("대기열에서 고객을 꺼내려고 할 때")
    class 대기열에서_고객을_꺼내려고할때 {

        @Test
        @DisplayName("활성화된 토큰이 10분이 지나지 않았다면 유효하다.")
        void 활성화된_토큰이_10분이_지나지_않았다면_유효하다() {
            Concert concert = Concert.of("박효신 콘서트");
            Concert savedConcert = concertRepository.save(concert);

            String uuid = UUID.randomUUID().toString();
            String token = RandomStringGenerator.generateRandomString(16);

            WaitingQueue activeToken  = WaitingQueue.of(savedConcert, uuid, token, 0);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
            activeToken.activateToken(fiveMinutesAgo);
            waitingQueueRepository.save(activeToken);

            sut.processNextCustomer(savedConcert.getId());
            assertEquals(0, activeToken.getWaitingNumber());
            assertEquals(WaitingQueueStatus.ACTIVE, activeToken.getStatus());
        }

        @Test
        @DisplayName("활성화된 토큰이 10분이 지났다면 무효화하고, 새 토큰을 대기열에서 꺼낸다.")
        void 활성화된_토큰이_10분이_지났다면_무효화하고_새_토큰을_대기열에서_꺼낸다() {
            Concert concert = Concert.of("박효신 콘서트");
            Concert savedConcert = concertRepository.save(concert);

            String uuid1 = UUID.randomUUID().toString();
            String token1 = RandomStringGenerator.generateRandomString(16);

            WaitingQueue activeToken  = WaitingQueue.of(savedConcert, uuid1, token1, 0);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime fifteenMinutesAgo = now.minusMinutes(15);
            activeToken.activateToken(fifteenMinutesAgo);
            waitingQueueRepository.save(activeToken);

            String uuid2 = UUID.randomUUID().toString();
            String token2 = RandomStringGenerator.generateRandomString(16);
            WaitingQueue firstToken  = WaitingQueue.of(savedConcert, uuid2, token2, 1);
            waitingQueueRepository.save(firstToken);

            sut.processNextCustomer(savedConcert.getId());
            assertEquals(-1, activeToken.getWaitingNumber());
            assertEquals(WaitingQueueStatus.DONE, activeToken.getStatus());
            assertEquals(0, firstToken.getWaitingNumber());
            assertEquals(WaitingQueueStatus.ACTIVE, firstToken.getStatus());
        }
    }

    @Nested
    @DisplayName("결제가 완료되었을 때")
    class 결제가_완료되었을_때 {

        @Test
        @DisplayName("토큰의 상태가 DONE으로 업데이트된다.")
        void 토큰의_상태가_DONE으로_업데이트된다() {
            Concert concert = Concert.of("박효신 콘서트");
            Concert savedConcert = concertRepository.save(concert);

            String uuid = UUID.randomUUID().toString();
            String token = RandomStringGenerator.generateRandomString(16);

            WaitingQueue activeToken = WaitingQueue.of(savedConcert, uuid, token, 0);
            LocalDateTime dateTime = LocalDateTime.of(2024, 10, 18, 0, 0);
            activeToken.activateToken(dateTime);
            waitingQueueRepository.save(activeToken);

            sut.updateWaitingQueueStatus(token);

            assertEquals(-1, activeToken.getWaitingNumber());
            assertEquals(WaitingQueueStatus.DONE, activeToken.getStatus());
        }
    }
}
