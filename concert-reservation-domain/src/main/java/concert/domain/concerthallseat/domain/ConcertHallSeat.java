package concert.domain.concerthallseat.domain;

import concert.domain.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert_hall_seat")
@NoArgsConstructor
public class ConcertHallSeat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "concert_hall_id")
    private long concertHallId;

    private long number;
    @Builder
    public ConcertHallSeat(long concertHallId, long number){
        this.concertHallId = concertHallId;
        this.number = number;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static ConcertHallSeat of(long concertHallId, long number){
        return ConcertHallSeat.builder()
                              .concertHallId(concertHallId)
                              .number(number)
                              .build();
    }
}
