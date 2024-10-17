package com.example.concert.member.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRequest {

    private String name;

    @Builder
    public MemberRequest(String name){
        this.name = name;
    }
}
