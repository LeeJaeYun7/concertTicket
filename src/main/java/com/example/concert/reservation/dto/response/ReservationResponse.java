package com.example.concert.reservation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationResponse {

    private final String name;

    private final String concertName;
    private final LocalDateTime dateTime;
    private final long price;

    @Builder
    public ReservationResponse(String name, String concertName, LocalDateTime dateTime, long price){
        this.name = name;
        this.concertName = concertName;
        this.dateTime = dateTime;
        this.price = price;
    }

    public static ReservationResponse of(String name, String concertName, LocalDateTime dateTime, long price){
        return ReservationResponse.builder()
                                  .name(name)
                                  .concertName(concertName)
                                  .dateTime(dateTime)
                                  .price(price)
                                  .build();
    }
}
