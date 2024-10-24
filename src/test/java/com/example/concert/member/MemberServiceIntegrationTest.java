package com.example.concert.member;

import com.example.concert.member.domain.Member;
import com.example.concert.member.repository.MemberRepository;
import com.example.concert.member.service.MemberService;
import com.example.concert.member.vo.MemberVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class MemberServiceIntegrationTest {

    @Autowired
    private MemberService sut;

    @Autowired
    private MemberRepository memberRepository;


    @Nested
    @DisplayName("멤버를 생성할 때")
    class 멤버를_생성할때 {

        @Test
        @DisplayName("name이 전달될 때, 멤버가 생성된다")
        void name이_전달될때_멤버가_생성된다() {
            MemberVO memberVO = sut.createMember("Tom Cruise");
            assertEquals("Tom Cruise", memberVO.getName());
        }
    }

    @Nested
    @DisplayName("멤버를 조회할 때")
    class 멤버를_조회할때 {

        @Test
        @DisplayName("name이 전달될 때, uuid가 조회된다")
        void name이_전달될때_uuid가_조회된다() {
            String name = "Tom Cruise";
            sut.createMember(name);

            MemberVO memberVO = sut.getMemberUuid(name);
            assertNotNull(memberVO.getUuid());
        }

        @Test
        @DisplayName("uuid가 전달될 때, 멤버가 조회된다")
        void uuid가_전달될때_멤버가_조회된다() {
            String name = "Tom Cruise";
            MemberVO memberVO = sut.createMember(name);

            Member member = sut.getMemberByUuid(memberVO.getUuid());
            assertEquals("Tom Cruise", member.getName());
        }

        @Test
        @DisplayName("uuid가 전달될 때, 멤버가 비관적 락을 통해 조회된다")
        void uuid가_전달될때_멤버가_비관적_락을_통해_조회된다() {
            String name = "Tom Cruise";
            MemberVO memberVO = sut.createMember(name);

            Member member = sut.getMemberByUuidWithLock(memberVO.getUuid());
            assertEquals("Tom Cruise", member.getName());
        }
    }
}
