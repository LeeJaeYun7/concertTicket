package concert.domain.concert.entities;

import concert.domain.shared.entities.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert_schedule")
@NoArgsConstructor
public class ConcertScheduleEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "concert_id")
  private long concertId;

  private LocalDateTime dateTime;

  @Builder
  public ConcertScheduleEntity(long concertId, LocalDateTime dateTime) {
    this.concertId = concertId;
    this.dateTime = dateTime;
    this.setCreatedAt(LocalDateTime.now());
    this.setUpdatedAt(LocalDateTime.now());
  }

  public static ConcertScheduleEntity of(long concertId, LocalDateTime dateTime) {
    return ConcertScheduleEntity.builder()
                                .concertId(concertId)
                                .dateTime(dateTime)
                                .build();
  }

  public void setConcertId(long concertId){
    this.concertId = concertId;
  }
}
