package com.example.concert.concertschedule;


import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.repository.ConcertScheduleRepository;
import com.example.concert.concertschedule.service.ConcertScheduleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ConcertScheduleIntegrationTest {

    @Autowired
    private ConcertScheduleService sut;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Nested
    @DisplayName("현재 이후의 모든 콘서트 스케줄을 가져올 때")
    class 현재_이후의_모든_콘서트_스케줄을_가져올때 {
        @Test
        @DisplayName("총 스케줄이_2개인 경우 2개를 가져온다.")
        void 총_스케줄이_2개인_경우_2개를_가져온다() {
            Concert concert1 = Concert.of("김연우 콘서트");
            LocalDateTime dateTime1 = LocalDateTime.of(2024, 11, 25, 22, 30);
            ConcertSchedule concertSchedule1 = ConcertSchedule.of(concert1, dateTime1, 50000);
            LocalDateTime dateTime2 = LocalDateTime.of(2024, 11, 28, 22, 30);
            ConcertSchedule concertSchedule2 = ConcertSchedule.of(concert1, dateTime2, 50000);

            Concert saveConcert = concertRepository.save(concert1);
            concertScheduleRepository.save(concertSchedule1);
            concertScheduleRepository.save(concertSchedule2);

            List<ConcertSchedule> result = sut.getAllConcertSchedulesAfterNowByConcertId(saveConcert.getId());

            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("총 스케줄이_2개인 경우 해당하는 1개를 가져온다.")
        void 총_스케줄이_2개인_경우_해당하는_1개를_가져온다() {
            Concert concert1 = Concert.of("김연우 콘서트");
            LocalDateTime dateTime1 = LocalDateTime.of(2024, 9, 25, 22, 30);
            ConcertSchedule concertSchedule1 = ConcertSchedule.of(concert1, dateTime1, 50000);
            LocalDateTime dateTime2 = LocalDateTime.of(2024, 11, 28, 22, 30);
            ConcertSchedule concertSchedule2 = ConcertSchedule.of(concert1, dateTime2, 50000);

            Concert saveConcert = concertRepository.save(concert1);
            concertScheduleRepository.save(concertSchedule1);
            concertScheduleRepository.save(concertSchedule2);

            List<ConcertSchedule> result = sut.getAllConcertSchedulesAfterNowByConcertId(saveConcert.getId());

            assertEquals(1, result.size());
        }
    }
}
