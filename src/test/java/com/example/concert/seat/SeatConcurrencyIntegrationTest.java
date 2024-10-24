package com.example.concert.seat;

import com.example.concert.seat.service.SeatFacade;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.concertschedule.repository.ConcertScheduleRepository;
import com.example.concert.member.domain.Member;
import com.example.concert.member.repository.MemberRepository;
import com.example.concert.member.service.MemberService;
import com.example.concert.seat.domain.Seat;
import com.example.concert.seat.domain.SeatStatus;
import com.example.concert.seat.repository.SeatRepository;
import com.example.concert.utils.RandomStringGenerator;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
public class SeatConcurrencyIntegrationTest {
    @Autowired
    private SeatFacade seatFacade;

    @Autowired
    private MemberService memberService;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    ConcertScheduleRepository concertScheduleRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SeatRepository seatRepository;

    private String token;
    private Member savedMember;
    private String memberUuid;

    private Concert savedConcert;

    private ConcertSchedule savedConcertSchedule;

    private Seat savedSeat;

    @BeforeEach
    void setUp() {
        Member member = Member.of("Tom Cruise");
        member.updateBalance(100000);
        savedMember = memberRepository.save(member);
        token = RandomStringGenerator.generateRandomString(16);
        memberUuid = savedMember.getUuid();

        Concert concert = Concert.of("김연우 콘서트");
        savedConcert = concertRepository.save(concert);

        LocalDateTime dateTime = LocalDateTime.of(2024, 11, 25, 22, 30);
        ConcertSchedule concertSchedule = ConcertSchedule.of(savedConcert, dateTime, 50000);
        savedConcertSchedule = concertScheduleRepository.save(concertSchedule);

        Seat seat = Seat.of(savedConcertSchedule, 1, 50000, SeatStatus.AVAILABLE);
        seat.setUpdatedAt(LocalDateTime.now().minusMinutes(10));
        savedSeat = seatRepository.save(seat);
    }

    @AfterEach
    void databaseCleansing() {
        seatRepository.deleteById(savedSeat.getId());
        concertScheduleRepository.deleteById(savedConcertSchedule.getId());
        concertRepository.deleteById(savedConcert.getId());
        memberRepository.deleteById(savedMember.getId());
    }

    @Nested
    @DisplayName("여러 멤버가 같은 예약 요청을 여러 번 보낼 때")
    class 여러_멤버가_같은_좌석_예약_요청을_여러_번_보낼때 {
        @Test
        @DisplayName("총 3번의 좌석 예약 요청 중 1번만 성공한다")
        void 총_3번의_좌석_예약_요청_중_1번만_성공한다() throws InterruptedException {
            int requestCount = 3;
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    try {
                        seatFacade.createSeatReservation(memberUuid, savedConcertSchedule.getId(), savedSeat.getNumber());
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            assertEquals(1, successCount.get());
        }
    }
}

