package com.example.concert.concert.charge;

import com.example.concert.charge.service.ChargeFacade;
import com.example.concert.member.domain.Member;
import com.example.concert.member.repository.MemberRepository;
import com.example.concert.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ChargeConcurrencyIntegrationTest {

    @Autowired
    private ChargeFacade chargeFacade;

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    private String memberUuid;

    @BeforeEach
    void setUp() {
        Member member = Member.of("Tom Cruise");
        Member savedMember = memberRepository.save(member);
        memberUuid = savedMember.getUuid();
    }

    @Nested
    @DisplayName("멤버가 같은 충전 요청을 여러 번 보낼 때")
    class 멤버가_같은_충전_요청을_여러_번_보낼때 {
        @Test
        @DisplayName("총 3번의 충전 요청 중 1번만 멤버 잔액에 반영된다")
        void 총_3번의_충전_요청_중_1번만_멤버_잔액에_반영된다() throws InterruptedException {
            int requestCount = 3;
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AtomicInteger successCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(requestCount);

            for (int i = 0; i < requestCount; i++) {
                executorService.submit(() -> {
                    try {
                        chargeFacade.chargeBalance(memberUuid, 10000);
                        successCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            Member updatedMember = memberRepository.findByUuid(memberUuid).orElseThrow();

            assertEquals(1, successCount.get());
            assertEquals(10000, updatedMember.getBalance());
        }
    }
}

