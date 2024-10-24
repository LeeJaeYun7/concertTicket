package com.example.concert.charge.domain;

import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;

import java.util.UUID;

@Entity
@Table(name = "charge")
public class Charge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String uuid;

    private long amount;

    @Builder
    public Charge(String uuid, long amount){
        this.uuid = uuid;
        this.amount = amount;
    }

    public static Charge of(String uuid, long amount){
        return Charge.builder()
                     .uuid(uuid)
                     .amount(amount)
                     .build();
    }
}
