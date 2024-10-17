package com.example.concert.reservation.domain;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.global.entity.BaseTimeEntity;
import com.example.concert.seat.domain.Seat;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "reservation")
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;

    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private long price;

    @Builder
    public Reservation(ConcertSchedule concertSchedule, UUID uuid, Seat seat, long price){
        this.concertSchedule = concertSchedule;
        this.uuid = uuid;
        this.seat = seat;
        this.price = price;
    }

    public static Reservation of(ConcertSchedule concertSchedule, UUID uuid, Seat seat, long price){
        return Reservation.builder()
                          .concertSchedule(concertSchedule)
                          .uuid(uuid)
                          .seat(seat)
                          .price(price)
                          .build();
    }
}
