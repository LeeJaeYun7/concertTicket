package com.example.concert.concertschedule.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ConcertScheduleResponse {

    private final List<LocalDateTime> availableDateTimes;

    @Builder
    public ConcertScheduleResponse(List<LocalDateTime> availableDateTimes){
        this.availableDateTimes = availableDateTimes;
    }

    public static ConcertScheduleResponse of(List<LocalDateTime> availableDateTimes){
        return ConcertScheduleResponse.builder()
                                      .availableDateTimes(availableDateTimes)
                                      .build();
    }
}
