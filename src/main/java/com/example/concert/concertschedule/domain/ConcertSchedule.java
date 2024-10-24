package com.example.concert.concertschedule.domain;

import com.example.concert.concert.domain.Concert;
import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "concert_schedule")
@NoArgsConstructor
public class ConcertSchedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    private LocalDateTime dateTime;

    private long price;

    @Builder
    public ConcertSchedule(Concert concert, LocalDateTime dateTime, long price){
        this.concert = concert;
        this.dateTime = dateTime;
        this.price = price;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static ConcertSchedule of(Concert concert, LocalDateTime dateTime, long price){
        return ConcertSchedule.builder()
                              .concert(concert)
                              .dateTime(dateTime)
                              .price(price)
                              .build();
    }
}
