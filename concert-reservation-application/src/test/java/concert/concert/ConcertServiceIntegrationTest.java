package concert.concert;

import concert.concert.fixtures.ConcertEntityFixtureFactory;
import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.services.ConcertService;
import concert.domain.concert.cache.ConcertCache;
import concert.domain.concert.entities.dao.ConcertEntityDAO;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import concert.domain.shared.utils.TimeProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Disabled
public class ConcertServiceIntegrationTest {

  @Autowired
  private TimeProvider timeProvider;
  @Autowired
  private ReservationEntityDAO reservationEntityDAO;
  @Autowired
  private ConcertCache concertCache;
  @Autowired
  private ConcertService sut;

  @Autowired
  private ConcertEntityDAO concertEntityDAO;

  @Test
  @DisplayName("콘서트를 저장하고 가져온다")
  void 콘서트를_저장하고_가져온다() {
    ConcertEntity concert = ConcertEntityFixtureFactory.createConcertWithParameters(1L, "박효신 콘서트", 120);
    concertEntityDAO.save(concert);

    ConcertEntity foundConcert = sut.getConcertById(1L);

    assertThat(foundConcert).isNotNull();
    assertThat(foundConcert.getId()).isEqualTo(1L);
    assertThat(foundConcert.getName()).isEqualTo("박효신 콘서트");
  }
}
