package com.example.concert.member.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class MemberResponse {

    private String uuid;
    private String name;

    @Builder
    public MemberResponse(String uuid, String name){

        this.uuid = uuid;
        this.name = name;
    }

    public static MemberResponse of(String uuid, String name){
        return MemberResponse.builder()
                             .uuid(uuid)
                             .name(name)
                             .build();
    }
}
