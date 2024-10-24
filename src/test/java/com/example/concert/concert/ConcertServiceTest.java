package com.example.concert.concert;

import com.example.concert.concert.domain.Concert;
import com.example.concert.concert.fixtures.ConcertFixtureFactory;
import com.example.concert.concert.repository.ConcertRepository;
import com.example.concert.concert.service.ConcertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @InjectMocks
    private ConcertService sut;

    @Nested
    class 콘서트ID로_콘서트를_가져올때 {
        @Test
        @DisplayName("콘서트를 가져온다")
        void 콘서트를_가져온다() {
            Concert concert = ConcertFixtureFactory.createConcertWithIdAndName(1L, "박효신 콘서트");
            long concertId = 1L;
            given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));

            Concert foundConcert = sut.getConcertById(concertId);

            assertEquals(1L, foundConcert.getId());
            assertEquals("박효신 콘서트", foundConcert.getName());
        }
    }
}
