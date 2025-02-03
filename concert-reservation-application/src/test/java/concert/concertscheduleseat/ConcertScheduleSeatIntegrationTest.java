package concert.concertscheduleseat;

import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.dao.ConcertEntityDAO;
import concert.domain.concert.entities.dao.ConcertScheduleEntityDAO;
import concert.domain.concert.entities.dao.ConcertScheduleSeatEntityDAO;
import concert.domain.concert.entities.enums.ConcertAgeRestriction;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.dao.ConcertHallEntityDAO;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.concerthall.entities.dao.ConcertHallSeatEntityDAO;
import concert.domain.concert.services.ConcertScheduleSeatService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Disabled
public class ConcertScheduleSeatIntegrationTest {

  @Autowired
  private ConcertScheduleSeatService sut;

  @Autowired
  private ConcertEntityDAO concertEntityDAO;

  @Autowired
  private ConcertHallEntityDAO concertHallEntityDAO;

  @Autowired
  private ConcertScheduleEntityDAO concertScheduleEntityDAO;
  @Autowired
  private ConcertHallSeatEntityDAO concertHallSeatEntityDAO;
  @Autowired
  private ConcertScheduleSeatEntityDAO concertScheduleSeatEntityDAO;

  @Nested
  @DisplayName("예약 가능한 좌석을 조회할 때")
  class 예약_가능한_좌석을_조회할때 {

    @Test
    @DisplayName("concertScheduleId가 전달될 때, 예약 가능한 좌석이 조회된다")
    void concertScheduleId가_전달될때_예약_가능한_좌석이_조회된다() {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertHallEntity savedConcertHallEntity = concertHallEntityDAO.save(concertHallEntity);

      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", savedConcertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      concertEntityDAO.save(concert);

      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);
      ConcertScheduleEntity savedConcertSchedule = concertScheduleEntityDAO.save(concertSchedule);

      ConcertHallSeatEntity seat11 = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 11);
      seat11.setUpdatedAt(LocalDateTime.now().minusMinutes(10));
      ConcertHallSeatEntity seat22 = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 22);
      seat22.setUpdatedAt(LocalDateTime.now().minusMinutes(10));

      concertHallSeatEntityDAO.save(seat11);
      concertHallSeatEntityDAO.save(seat22);

      ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity allConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat11.getId(), concertSchedule.getId(), allSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
      ConcertSeatGradeEntity standingSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.STANDING, 80000);
      ConcertScheduleSeatEntity standingConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat22.getId(), concertSchedule.getId(), standingSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);

      concertScheduleSeatEntityDAO.save(allConcertScheduleSeat);
      concertScheduleSeatEntityDAO.save(standingConcertScheduleSeat);

      List<ConcertScheduleSeatEntity> result = sut.getAllAvailableConcertScheduleSeats(savedConcertSchedule.getId());
      assertEquals(result.get(0).getConcertHallSeatId(), seat11.getId());
      assertEquals(result.get(1).getConcertHallSeatId(), seat22.getId());
    }
  }

  @Nested
  @DisplayName("좌석의 업데이트 시각을 최신화할 때")
  class 업데이트_시각을_최신화할때 {

    @Test
    @DisplayName("업데이트 시각을 최신화할 때 성공한다")
    void 업데이트_시각을_최신화할때_성공한다() {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertHallEntity savedConcertHallEntity = concertHallEntityDAO.save(concertHallEntity);

      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", savedConcertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      concertEntityDAO.save(concert);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);
      ConcertScheduleEntity savedConcertSchedule = concertScheduleEntityDAO.save(concertSchedule);

      ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 1);
      concertHallSeatEntityDAO.save(seat);

      sut.changeStatusAndUpdatedAt(1L);

      assertEquals(seat.getUpdatedAt(), LocalDateTime.now());
    }
  }

  @Nested
  @DisplayName("좌석 상태를 업데이트할 때")
  class 좌석_상태를_업데이트할때 {
    @Test
    @DisplayName("좌석 상태를 업데이트할 때 성공한다")
    void 좌석_상태를_업데이트할_때_성공한다() {
      LocalDate startAt = LocalDate.of(2024, 10, 16);
      LocalDate endAt = LocalDate.of(2024, 10, 18);

      ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
      ConcertHallEntity savedConcertHallEntity = concertHallEntityDAO.save(concertHallEntity);
      ConcertEntity concert = ConcertEntity.of("박효신 콘서트", savedConcertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

      concertEntityDAO.save(concert);

      LocalDateTime dateTime = LocalDateTime.of(2024, 10, 16, 22, 30);
      ConcertScheduleEntity concertSchedule = ConcertScheduleEntity.of(concert.getId(), dateTime);
      ConcertScheduleEntity savedConcertSchedule = concertScheduleEntityDAO.save(concertSchedule);

      ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 11);
      concertHallSeatEntityDAO.save(seat);

      ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
      ConcertScheduleSeatEntity allConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat.getId(), concertSchedule.getId(), allSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
      concertScheduleSeatEntityDAO.save(allConcertScheduleSeat);

      sut.updateConcertScheduleSeatStatus(11, ConcertScheduleSeatStatus.RESERVED);

      assertEquals(allConcertScheduleSeat.getStatus(), ConcertScheduleSeatStatus.RESERVED);
    }
  }
}
