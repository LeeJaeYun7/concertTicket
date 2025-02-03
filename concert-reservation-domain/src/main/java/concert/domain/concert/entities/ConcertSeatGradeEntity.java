package concert.domain.concert.entities;

import concert.domain.concert.entities.enums.SeatGrade;
import concert.domain.shared.entities.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert_seat_grade")
@NoArgsConstructor
public class ConcertSeatGradeEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "concert_id")
  private long concertId;

  @Enumerated(EnumType.STRING)
  @Column(name = "grade")
  private SeatGrade seatGrade;

  private long price;

  @Builder
  public ConcertSeatGradeEntity(long concertId, SeatGrade seatGrade, long price) {
    this.concertId = concertId;
    this.seatGrade = seatGrade;
    this.price = price;
    this.setCreatedAt(LocalDateTime.now());
    this.setUpdatedAt(LocalDateTime.now());
  }

  public static ConcertSeatGradeEntity of(long concertId, SeatGrade seatGrade, long price) {
    return ConcertSeatGradeEntity.builder()
                          .concertId(concertId)
                          .seatGrade(seatGrade)
                          .price(price)
                          .build();
  }
}
