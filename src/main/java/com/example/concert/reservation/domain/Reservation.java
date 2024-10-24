package com.example.concert.reservation.domain;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.global.entity.BaseTimeEntity;
import com.example.concert.seat.domain.Seat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reservation")
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;

    private String uuid;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private long price;

    @Builder
    public Reservation(ConcertSchedule concertSchedule, String uuid, Seat seat, long price){
        this.concertSchedule = concertSchedule;
        this.uuid = uuid;
        this.seat = seat;
        this.price = price;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static Reservation of(ConcertSchedule concertSchedule, String uuid, Seat seat, long price){
        return Reservation.builder()
                          .concertSchedule(concertSchedule)
                          .uuid(uuid)
                          .seat(seat)
                          .price(price)
                          .build();
    }
}
