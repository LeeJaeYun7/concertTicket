package com.example.concert.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class MemberResponse {

    private UUID uuid;

    @Builder
    public MemberResponse(UUID uuid){
        this.uuid = uuid;
    }

    public static MemberResponse of(UUID uuid){
        return MemberResponse.builder()
                             .uuid(uuid)
                             .build();
    }
}
