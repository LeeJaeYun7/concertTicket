package com.example.concert.concertschedule;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.repository.ConcertScheduleRepository;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import com.example.concert.utils.TimeProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConcertScheduleServiceTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @InjectMocks
    private ConcertScheduleService sut;


    @Nested
    @DisplayName("현재 이후의 모든 콘서트 스케줄을 가져올 때")
    class 현재_이후의_모든_콘서트_스케줄을_가져올때 {
        @Test
        @DisplayName("성공한다")
        void 성공한다() {
            Concert concert1 = Concert.of("박효신 콘서트");
            LocalDateTime dateTime1 = LocalDateTime.of(2024, 10, 16, 22, 30);
            ConcertSchedule concertSchedule1 = ConcertSchedule.of(concert1, dateTime1, 50000);

            Concert concert2 = Concert.of("아이유 콘서트");
            LocalDateTime dateTime2 = LocalDateTime.of(2024, 10, 18, 22, 30);
            ConcertSchedule concertSchedule2 = ConcertSchedule.of(concert2, dateTime2, 50000);

            when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 10, 18, 0, 0));

            given(concertScheduleRepository.findAllAfterNowByConcertId(1L, timeProvider.now())).willReturn(List.of(concertSchedule2));

            List<ConcertSchedule> result = sut.getAllConcertSchedulesAfterNowByConcertId(1L);

            assertEquals(1, result.size());
        }
    }
}
