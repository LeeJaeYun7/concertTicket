package com.example.concert.concert.domain;

import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "concert")
@NoArgsConstructor
public class Concert extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Builder
    public Concert(String name){
        this.name = name;
    }

    public static Concert of(String name){
        return Concert.builder()
                      .name(name)
                      .build();
    }
}
