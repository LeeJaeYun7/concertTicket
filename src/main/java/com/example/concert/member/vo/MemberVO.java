package com.example.concert.member.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MemberVO {

    private final UUID uuid;
    private final String name;


    @Builder
    public MemberVO(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
    }

    public static MemberVO of(UUID uuid, String name){
        return MemberVO.builder()
                       .uuid(uuid)
                       .name(name)
                       .build();
    }
}
