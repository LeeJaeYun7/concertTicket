package com.example.concert.payment.domain;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "payment")
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id")
    private ConcertSchedule concertSchedule;
    private UUID uuid;
    private long amount;

    @Builder
    public Payment(ConcertSchedule concertSchedule, UUID uuid, long amount){
        this.concertSchedule = concertSchedule;
        this.uuid = uuid;
        this.amount = amount;
    }

    public static Payment of(ConcertSchedule concertSchedule, UUID uuid, long amount){
        return Payment.builder()
                      .concertSchedule(concertSchedule)
                      .uuid(uuid)
                      .amount(amount)
                      .build();
    }
}
