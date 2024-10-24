package com.example.concert.payment.domain;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payment")
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;
    private String uuid;
    private long amount;

    @Builder
    public Payment(ConcertSchedule concertSchedule, String uuid, long amount){
        this.concertSchedule = concertSchedule;
        this.uuid = uuid;
        this.amount = amount;
        this.setCreatedAt(LocalDateTime.now());
        this.setUpdatedAt(LocalDateTime.now());
    }

    public static Payment of(ConcertSchedule concertSchedule, String uuid, long amount){
        return Payment.builder()
                      .concertSchedule(concertSchedule)
                      .uuid(uuid)
                      .amount(amount)
                      .build();
    }
}
