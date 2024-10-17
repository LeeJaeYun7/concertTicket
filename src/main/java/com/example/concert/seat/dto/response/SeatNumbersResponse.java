package com.example.concert.seat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SeatNumbersResponse {

    private final List<Long> availableSeatNumbers;

    @Builder
    public SeatNumbersResponse(List<Long> availableSeatNumbers){
        this.availableSeatNumbers = availableSeatNumbers;
    }

    public static SeatNumbersResponse of(List<Long> availableSeatNumbers){
        return SeatNumbersResponse.builder()
                            .availableSeatNumbers(availableSeatNumbers)
                            .build();
    }
}
