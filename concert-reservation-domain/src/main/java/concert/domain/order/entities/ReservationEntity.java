package concert.domain.order.entities;

import concert.domain.order.entities.enums.ReservationStatus;
import concert.domain.shared.entities.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "reservation", indexes = {@Index(name = "idx_created_at_concert_id", columnList = "created_at, concert_id")})
public class ReservationEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "order_id")
  private long orderId;

  @Column(name = "concert_id")
  private long concertId;

  @Column(name = "concert_schedule_seat_id")
  private long concertScheduleSeatId;

  @Enumerated(EnumType.STRING)
  private ReservationStatus reservationStatus;

  @Column(name = "price")
  private long price;

  @Builder
  public ReservationEntity(long orderId, long concertId, long concertScheduleSeatId, ReservationStatus reservationStatus, long price) {
    this.orderId = orderId;
    this.concertId = concertId;
    this.concertScheduleSeatId = concertScheduleSeatId;
    this.reservationStatus = reservationStatus;
    this.price = price;
    this.setCreatedAt(LocalDateTime.now());
    this.setUpdatedAt(LocalDateTime.now());
  }

  public static ReservationEntity of(long orderId, long concertId, long concertScheduleSeatId, ReservationStatus reservationStatus, long price) {
    return ReservationEntity.builder()
                            .orderId(orderId)
                            .concertId(concertId)
                            .concertScheduleSeatId(concertScheduleSeatId)
                            .reservationStatus(reservationStatus)
                            .price(price)
                            .build();
  }
}
