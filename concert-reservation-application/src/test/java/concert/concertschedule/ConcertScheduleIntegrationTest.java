package concert.concertschedule;

import concert.domain.concert.entities.ConcertEntity;
import concert.domain.concert.entities.ConcertScheduleEntity;
import concert.domain.concert.entities.ConcertScheduleSeatEntity;
import concert.domain.concert.entities.ConcertSeatGradeEntity;
import concert.domain.concert.entities.dao.ConcertEntityDAO;
import concert.domain.concert.entities.dao.ConcertScheduleEntityDAO;
import concert.domain.concert.entities.dao.ConcertScheduleSeatEntityDAO;
import concert.domain.concert.entities.dao.ConcertSeatGradeEntityDAO;
import concert.domain.concert.entities.enums.ConcertAgeRestriction;
import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.concert.services.ConcertScheduleService;
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.dao.ConcertHallEntityDAO;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.concerthall.entities.dao.ConcertHallSeatEntityDAO;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import concert.domain.shared.utils.TimeProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Disabled
public class ConcertScheduleIntegrationTest {

  @Autowired
  TimeProvider timeProvider;
  LocalDate startAt;
  LocalDate endAt;
  ConcertHallEntity concertHallEntity;
  ConcertHallEntity savedConcertHallEntity;
  ConcertEntity firstConcert;
  ConcertEntity secondConcert;
  ConcertEntity savedFirstConcert;
  ConcertEntity savedSecondConcert;
  ConcertScheduleEntity firstConcertSchedule;
  ConcertScheduleEntity secondConcertSchedule;
  ConcertScheduleEntity savedFirstConcertSchedule;
  ConcertScheduleEntity savedSecondConcertSchedule;
  ConcertScheduleEntity thirdConcertSchedule;
  ConcertScheduleEntity fourthConcertSchedule;
  ConcertScheduleEntity savedThirdConcertSchedule;
  ConcertScheduleEntity savedFourthConcertSchedule;
  LocalDateTime firstDateTime;
  LocalDateTime secondDateTime;
  @Autowired
  private ConcertScheduleService sut;
  @Autowired
  private ConcertHallSeatEntityDAO concertHallSeatEntityDAO;
  @Autowired
  private ReservationEntityDAO reservationEntityDAO;
  @Autowired
  private ConcertEntityDAO concertEntityDAO;
  @Autowired
  private ConcertHallEntityDAO concertHallEntityDAO;
  @Autowired
  private ConcertScheduleEntityDAO concertScheduleEntityDAO;
  @Autowired
  private ConcertScheduleSeatEntityDAO concertScheduleSeatEntityDAO;
  @Autowired
  private ConcertSeatGradeEntityDAO concertSeatGradeEntityDAO;

  @BeforeEach
  void setUp() {

    // 첫번째 테스트

    startAt = LocalDate.of(2024, 12, 1);
    endAt = LocalDate.of(2024, 12, 31);
    concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
    savedConcertHallEntity = concertHallEntityDAO.save(concertHallEntity);

    firstConcert = ConcertEntity.of("김연우 콘서트", savedConcertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);
    secondConcert = ConcertEntity.of("박효신 콘서트", savedConcertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);

    firstDateTime = LocalDateTime.of(2024, 12, 11, 22, 30);
    secondDateTime = LocalDateTime.of(2024, 12, 12, 22, 30);

    savedFirstConcert = concertEntityDAO.save(firstConcert);
    firstConcertSchedule = ConcertScheduleEntity.of(savedFirstConcert.getId(), firstDateTime);
    secondConcertSchedule = ConcertScheduleEntity.of(savedFirstConcert.getId(), secondDateTime);

    savedFirstConcertSchedule = concertScheduleEntityDAO.save(firstConcertSchedule);
    savedSecondConcertSchedule = concertScheduleEntityDAO.save(secondConcertSchedule);

    ConcertHallSeatEntity seat11 = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 11); // 예시로 Seat 객체를 생성
    ConcertHallSeatEntity seat12 = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 12);
    ConcertHallSeatEntity savedSeat11 = concertHallSeatEntityDAO.save(seat11);
    ConcertHallSeatEntity savedSeat12 = concertHallSeatEntityDAO.save(seat12);

    ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(savedFirstConcert.getId(), SeatGrade.ALL, 100000); // 예시로 SeatGrade 생성
    ConcertSeatGradeEntity savedALLSeatGrade = concertSeatGradeEntityDAO.save(allSeatGrade);

    LocalDateTime now = timeProvider.now();
    LocalDateTime threshold = now.minusMinutes(10);

    ConcertScheduleSeatEntity firstScheduleConcertScheduleSeat1 = ConcertScheduleSeatEntity.of(savedSeat11.getId(), savedFirstConcertSchedule.getId(), savedALLSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    ConcertScheduleSeatEntity firstScheduleConcertScheduleSeat2 = ConcertScheduleSeatEntity.of(savedSeat12.getId(), savedFirstConcertSchedule.getId(), savedALLSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    firstScheduleConcertScheduleSeat1.changeUpdatedAt(threshold);
    firstScheduleConcertScheduleSeat2.changeUpdatedAt(threshold);

    ConcertScheduleSeatEntity secondScheduleSeatInfo1 = ConcertScheduleSeatEntity.of(savedSeat11.getId(), savedSecondConcertSchedule.getId(), savedALLSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    ConcertScheduleSeatEntity secondScheduleSeatInfo2 = ConcertScheduleSeatEntity.of(savedSeat12.getId(), savedSecondConcertSchedule.getId(), savedALLSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    secondScheduleSeatInfo1.changeUpdatedAt(threshold);
    secondScheduleSeatInfo2.changeUpdatedAt(threshold);

    concertScheduleSeatEntityDAO.save(firstScheduleConcertScheduleSeat1);
    concertScheduleSeatEntityDAO.save(firstScheduleConcertScheduleSeat2);
    concertScheduleSeatEntityDAO.save(secondScheduleSeatInfo1);
    concertScheduleSeatEntityDAO.save(secondScheduleSeatInfo2);

    // 두번째 테스트

    savedSecondConcert = concertEntityDAO.save(secondConcert);
    thirdConcertSchedule = ConcertScheduleEntity.of(savedSecondConcert.getId(), firstDateTime);
    fourthConcertSchedule = ConcertScheduleEntity.of(savedSecondConcert.getId(), secondDateTime);

    ConcertScheduleEntity savedThirdConcertSchedule = concertScheduleEntityDAO.save(thirdConcertSchedule);
    ConcertScheduleEntity savedFourthConcertSchedule = concertScheduleEntityDAO.save(fourthConcertSchedule);

    ConcertHallSeatEntity seat21 = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 21); // 예시로 Seat 객체를 생성
    ConcertHallSeatEntity seat22 = ConcertHallSeatEntity.of(savedConcertHallEntity.getId(), 22);
    ConcertHallSeatEntity savedSeat21 = concertHallSeatEntityDAO.save(seat21);
    ConcertHallSeatEntity savedSeat22 = concertHallSeatEntityDAO.save(seat22);

    ConcertSeatGradeEntity RSeatGrade = ConcertSeatGradeEntity.of(savedSecondConcert.getId(), SeatGrade.ALL, 100000); // 예시로 SeatGrade 생성
    ConcertSeatGradeEntity savedRSeatGrade = concertSeatGradeEntityDAO.save(RSeatGrade);

    ConcertScheduleSeatEntity thirdConcertScheduleSeat1 = ConcertScheduleSeatEntity.of(savedSeat21.getId(), savedThirdConcertSchedule.getId(), savedALLSeatGrade.getId(), ConcertScheduleSeatStatus.RESERVED);
    ConcertScheduleSeatEntity thirdConcertScheduleSeat2 = ConcertScheduleSeatEntity.of(savedSeat22.getId(), savedThirdConcertSchedule.getId(), savedALLSeatGrade.getId(), ConcertScheduleSeatStatus.RESERVED);
    thirdConcertScheduleSeat1.changeUpdatedAt(threshold);
    thirdConcertScheduleSeat2.changeUpdatedAt(threshold);

    ConcertScheduleSeatEntity fourthConcertScheduleSeat1 = ConcertScheduleSeatEntity.of(savedSeat21.getId(), savedFourthConcertSchedule.getId(), savedRSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    ConcertScheduleSeatEntity fourthConcertScheduleSeat2 = ConcertScheduleSeatEntity.of(savedSeat22.getId(), savedFourthConcertSchedule.getId(), savedRSeatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    fourthConcertScheduleSeat1.changeUpdatedAt(threshold);
    fourthConcertScheduleSeat2.changeUpdatedAt(threshold);

    concertScheduleSeatEntityDAO.save(thirdConcertScheduleSeat1);
    concertScheduleSeatEntityDAO.save(thirdConcertScheduleSeat2);
    concertScheduleSeatEntityDAO.save(fourthConcertScheduleSeat1);
    concertScheduleSeatEntityDAO.save(fourthConcertScheduleSeat2);
  }


  @Nested
  @DisplayName("현재 이후의 모든 콘서트 스케줄을 가져올 때")
  class 현재_이후의_모든_콘서트_스케줄을_가져올때 {
    @Test
    @DisplayName("총 스케줄이_2개인 경우 2개를 가져온다.")
    void 총_스케줄이_2개인_경우_2개를_가져온다() {
      List<LocalDateTime> result = sut.getActiveConcertSchedules(savedFirstConcert.getId());
      assertEquals(2, result.size());
    }

    @Test
    @DisplayName("총 스케줄이_2개인 경우 해당하는 1개를 가져온다.")
    void 총_스케줄이_2개인_경우_해당하는_1개를_가져온다() {
      List<LocalDateTime> result = sut.getActiveConcertSchedules(savedSecondConcert.getId());
      assertEquals(1, result.size());
    }
  }
}
