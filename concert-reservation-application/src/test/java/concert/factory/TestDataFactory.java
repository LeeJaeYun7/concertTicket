package concert.factory;

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
import concert.domain.concerthall.entities.ConcertHallEntity;
import concert.domain.concerthall.entities.dao.ConcertHallEntityDAO;
import concert.domain.concerthall.entities.ConcertHallSeatEntity;
import concert.domain.concerthall.entities.dao.ConcertHallSeatEntityDAO;
import concert.domain.member.entities.MemberEntity;
import concert.domain.member.entities.dao.MemberRepository;
import concert.domain.order.entities.dao.ReservationEntityDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
@Component
public class TestDataFactory {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private ConcertEntityDAO concertEntityDAO;

  @Autowired
  private ConcertHallEntityDAO concertHallEntityDAO;
  @Autowired
  private ConcertScheduleEntityDAO concertScheduleEntityDAO;
  @Autowired
  private ConcertHallSeatEntityDAO concertHallSeatEntityDAO;

  @Autowired
  private ConcertSeatGradeEntityDAO concertSeatGradeEntityDAO;

  @Autowired
  private ConcertScheduleSeatEntityDAO concertScheduleSeatEntityDAO;

  @Autowired
  private ReservationEntityDAO reservationEntityDAO;


  public MemberEntity createMember() {
    MemberEntity member = MemberEntity.of("Tom Cruise");
    return memberRepository.save(member);
  }

  public ConcertHallEntity createConcertHall() {
    ConcertHallEntity concertHallEntity = ConcertHallEntity.of("KSPO DOME", "서울특별시 송파구 올림픽로 424 (방이동 88-2) 올림픽공원", "02-410-1114", null);
    return concertHallEntityDAO.save(concertHallEntity);
  }

  public ConcertEntity createConcert(ConcertHallEntity concertHallEntity) {
    LocalDate startAt = LocalDate.of(2024, 11, 25);
    LocalDate endAt = LocalDate.of(2024, 11, 28);
    ConcertEntity concert = ConcertEntity.of("브루노마스 콘서트", concertHallEntity.getId(), "ballad", 120, ConcertAgeRestriction.OVER_15, startAt, endAt);
    return concertEntityDAO.save(concert);
  }

  public ConcertScheduleEntity createConcertSchedule(ConcertEntity concert) {
    LocalDateTime dateTime = LocalDateTime.of(2024, 11, 25, 22, 30);
    return concertScheduleEntityDAO.save(ConcertScheduleEntity.of(concert.getId(), dateTime));
  }

  public ConcertHallSeatEntity createSeat(ConcertHallEntity concertHallEntity) {
    ConcertHallSeatEntity seat = ConcertHallSeatEntity.of(concertHallEntity.getId(), 1);
    return concertHallSeatEntityDAO.save(seat);
  }

  public ConcertSeatGradeEntity createSeatGrade(ConcertEntity concert) {
    ConcertSeatGradeEntity allSeatGrade = ConcertSeatGradeEntity.of(concert.getId(), SeatGrade.ALL, 100000);
    return concertSeatGradeEntityDAO.save(allSeatGrade);
  }

  public ConcertScheduleSeatEntity createConcertScheduleSeat(ConcertHallSeatEntity seat, ConcertScheduleEntity concertSchedule, ConcertSeatGradeEntity seatGrade) {
    ConcertScheduleSeatEntity allConcertScheduleSeat = ConcertScheduleSeatEntity.of(seat.getId(), concertSchedule.getId(), seatGrade.getId(), ConcertScheduleSeatStatus.AVAILABLE);
    return concertScheduleSeatEntityDAO.save(allConcertScheduleSeat);
  }
}
