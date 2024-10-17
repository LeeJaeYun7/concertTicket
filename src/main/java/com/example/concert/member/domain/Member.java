package com.example.concert.member.domain;

import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID uuid;

    private String name;

    private long balance;

    @Builder
    public Member(UUID uuid, String name, long balance){
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
    }

    public static Member of(String name){
        UUID uuid = UUID.randomUUID();

        return Member.builder()
                     .uuid(uuid)
                     .name(name)
                     .balance(0)
                     .build();
    }
    public void updateBalance(long balance){
        this.balance = balance;
    }
}

