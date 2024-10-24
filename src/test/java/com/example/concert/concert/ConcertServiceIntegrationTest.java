package com.example.concert.concert;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.fixtures.ConcertFixtureFactory;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.concert.service.ConcertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ConcertServiceIntegrationTest {

    @Autowired
    private ConcertService sut;

    @Autowired
    private ConcertRepository concertRepository;

    @Test
    @DisplayName("콘서트를 저장하고 가져온다")
    void 콘서트를_저장하고_가져온다() {
        Concert concert = ConcertFixtureFactory.createConcertWithParameters(1L, "박효신 콘서트");
        concertRepository.save(concert);

        Concert foundConcert = sut.getConcertById(1L);

        assertThat(foundConcert).isNotNull();
        assertThat(foundConcert.getId()).isEqualTo(1L);
        assertThat(foundConcert.getName()).isEqualTo("박효신 콘서트");
    }
}
