package com.example.concert.waitingQueue;

import com.example.concert.common.CustomException;
import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.fixtures.ConcertFixtureFactory;
import com.example.concert.concert.service.ConcertService;
import com.example.concert.utils.RandomStringGenerator;
import com.example.concert.waitingQueue.domain.WaitingQueue;
import com.example.concert.waitingQueue.service.WaitingQueueFacade;
import com.example.concert.waitingQueue.service.WaitingQueueService;
import com.example.concert.waitingQueue.vo.TokenVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WaitingQueueFacadeTest {

    @Mock
    private ConcertService concertService;

    @Mock
    private WaitingQueueService waitingQueueService;

    @InjectMocks
    private WaitingQueueFacade sut;



    @Nested
    @DisplayName("토큰 생성을 요청할 때")
    class 토큰_생성을_요청할_때 {

        @Test
        @DisplayName("어떤 대기열에도 토큰이 없다면, 토큰을 생성한다")
        void 어떤_대기열에도_토큰이_없다면_토큰을_생성한다() {
            UUID uuid = UUID.randomUUID();
            Concert concert1 = ConcertFixtureFactory.createConcertWithIdAndName(1L, "박효신 콘서트");

            UUID uuid1 = UUID.randomUUID();
            String token1 = RandomStringGenerator.generateRandomString(16);
            WaitingQueue element1 = WaitingQueue.of(concert1, uuid1, token1, 1);

            UUID uuid2 = UUID.randomUUID();
            String token2 = RandomStringGenerator.generateRandomString(16);
            WaitingQueue element2 = WaitingQueue.of(concert1, uuid2, token2, 2);

            given(waitingQueueService.getByUuid(uuid)).willReturn(Optional.empty());
            given(concertService.getConcertById(1L)).willReturn(concert1);
            given(waitingQueueService.getAllByConcertId(1L)).willReturn(List.of(element1, element2));

            TokenVO tokenVO = sut.createToken(1L, uuid);

            assertEquals(3, tokenVO.getWaitingNumber());
            verify(waitingQueueService, times(1)).save(any(WaitingQueue.class));
        }

        @Test
        @DisplayName("같은 대기열에 토큰이 있다면, 토큰을 생성하지 않는다")
        void 같은_대기열에_토큰이_있다면_토큰을_생성하지_않는다() {

            Concert concert = ConcertFixtureFactory.createConcertWithIdAndName(1L, "박효신 콘서트");
            UUID uuid = UUID.randomUUID();
            String token = RandomStringGenerator.generateRandomString(16);

            WaitingQueue element = WaitingQueue.of(concert, uuid, token, 1);
            given(waitingQueueService.getByUuid(uuid)).willReturn(Optional.of(element));

            assertThrows(CustomException.class, ()-> sut.createToken(1L, uuid));
        }

        @Test
        @DisplayName("다른 대기열에 토큰이 있다면, 해당 토큰을 삭제하고, 현재 대기열에 토큰을 생성한다")
        void 다른_대기열에_토큰이_있다면_해당_토큰을_삭제하고_현재_대기열에_토큰을_생성한다() {

            Concert concert1 = ConcertFixtureFactory.createConcertWithIdAndName(1L, "박효신 콘서트");
            Concert concert2 = ConcertFixtureFactory.createConcertWithIdAndName(2L, "아이유 콘서트");
            UUID uuid = UUID.randomUUID();
            String token = RandomStringGenerator.generateRandomString(16);

            WaitingQueue element = WaitingQueue.of(concert2, uuid, token, 10);

            UUID uuid1 = UUID.randomUUID();
            String token1 = RandomStringGenerator.generateRandomString(16);
            WaitingQueue element1 = WaitingQueue.of(concert1, uuid1, token1, 1);

            UUID uuid2 = UUID.randomUUID();
            String token2 = RandomStringGenerator.generateRandomString(16);
            WaitingQueue element2 = WaitingQueue.of(concert1, uuid2, token2, 2);

            given(waitingQueueService.getByUuid(uuid)).willReturn(Optional.of(element));
            given(concertService.getConcertById(1L)).willReturn(concert1);
            given(waitingQueueService.getAllByConcertId(1L)).willReturn(List.of(element1, element2));

            TokenVO tokenVO = sut.createToken(1L, uuid);

            assertEquals(3, tokenVO.getWaitingNumber());
            verify(waitingQueueService, times(1)).save(any(WaitingQueue.class));
        }
    }
}
