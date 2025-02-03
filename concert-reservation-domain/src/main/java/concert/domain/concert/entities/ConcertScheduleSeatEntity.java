package concert.domain.concert.entities;

import concert.domain.concert.entities.enums.ConcertScheduleSeatStatus;
import concert.domain.shared.entities.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert_schedule_seat")
@NoArgsConstructor
public class ConcertScheduleSeatEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "concert_hall_seat_id")
  private long concertHallSeatId;

  @Column(name = "concert_schedule_id")
  private long concertScheduleId;

  @Column(name = "seat_grade_id")
  private long concertSeatGradeId;

  @Enumerated(EnumType.STRING)
  private ConcertScheduleSeatStatus status;

  @Builder
  public ConcertScheduleSeatEntity(long concertHallSeatId, long concertScheduleId, long concertSeatGradeId, ConcertScheduleSeatStatus status) {
    this.concertHallSeatId = concertHallSeatId;
    this.concertScheduleId = concertScheduleId;
    this.concertSeatGradeId = concertSeatGradeId;
    this.status = status;
    this.setCreatedAt(LocalDateTime.now());
    this.setUpdatedAt(LocalDateTime.now());
  }

  public static ConcertScheduleSeatEntity of(long concertHallSeatId, long concertScheduleId, long concertSeatGradeId, ConcertScheduleSeatStatus status) {
    return ConcertScheduleSeatEntity.builder()
                                    .concertHallSeatId(concertHallSeatId)
                                    .concertScheduleId(concertScheduleId)
                                    .concertSeatGradeId(concertSeatGradeId)
                                    .status(status)
                                    .build();
  }

  public void updateStatus(ConcertScheduleSeatStatus status) {
    this.status = status;
  }

  public void changeUpdatedAt(LocalDateTime dateTime) {
      this.setUpdatedAt(dateTime);
  }
}