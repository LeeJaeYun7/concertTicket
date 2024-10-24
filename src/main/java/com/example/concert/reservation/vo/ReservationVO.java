package com.example.concert.reservation.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationVO {

    private final String name;
    private final String concertName;
    private final LocalDateTime dateTime;
    private final long price;

    @Builder
    public ReservationVO(String name, String concertName, LocalDateTime dateTime, long price) {
        this.name = name;
        this.concertName = concertName;
        this.dateTime = dateTime;
        this.price = price;
    }
    public static ReservationVO of(String name, String concertName, LocalDateTime dateTime, long price) {
        return ReservationVO.builder()
                            .name(name)
                            .concertName(concertName)
                            .dateTime(dateTime)
                            .price(price)
                            .build();
    }
}
