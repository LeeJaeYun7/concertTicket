package com.example.concert.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberBalanceResponse {
    private final long balance;

    @Builder
    public MemberBalanceResponse(long balance){
        this.balance = balance;
    }

    public static MemberBalanceResponse of(long balance){
        return MemberBalanceResponse.builder()
                                    .balance(balance)
                                    .build();
    }
}
