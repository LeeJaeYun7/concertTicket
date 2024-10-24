package com.example.concert.member;

import com.example.concert.common.CustomException;
import com.example.concert.member.domain.Member;
import com.example.concert.member.repository.MemberRepository;
import com.example.concert.member.service.MemberService;
import com.example.concert.member.vo.MemberVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService sut;

    @Nested
    @DisplayName("멤버를 생성할 때")
    class 멤버를_생성할때 {

        @Test
        @DisplayName("name이 전달될 때, 멤버가 생성된다")
        void name이_전달될때_멤버가_생성된다() {
            Member member = Member.of("Tom Cruise");

            given(memberRepository.save(any(Member.class))).willReturn(member);

            MemberVO memberVO = sut.createMember("Tom Cruise");

            assertEquals(memberVO.getUuid(), member.getUuid());
            verify(memberRepository, times(1)).save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("멤버를 조회할 때")
    class 멤버를_조회할때 {

        @Test
        @DisplayName("name이 전달될 때, uuid가 조회된다")
        void name이_전달될때_uuid가_조회된다() {
            String name = "Tom Cruise";
            Member member = Member.of(name);

            given(memberRepository.findByName(name)).willReturn(Optional.of(member));

            MemberVO memberVO = sut.getMemberUuid(name);

            assertEquals(memberVO.getUuid(), member.getUuid());
        }

        @Test
        @DisplayName("uuid가 전달될 때, 멤버가 조회된다")
        void uuid가_전달될때_멤버가_조회된다() {
            String name = "Tom Cruise";
            Member member = Member.of(name);
            UUID uuid = member.getUuid();

            given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(member));

            Member foundMember = sut.getMemberByUuid(uuid);

            assertEquals(foundMember.getUuid(), member.getUuid());
        }

        @Test
        @DisplayName("uuid가 전달될 때, 멤버가 비관적 락을 통해 조회된다")
        void uuid가_전달될때_멤버가_비관적_락을_통해_조회된다() {
            String name = "Tom Cruise";
            Member member = Member.of(name);
            UUID uuid = member.getUuid();

            given(memberRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(member));

            Member foundMember = sut.getMemberByUuidWithLock(uuid);

            assertEquals(foundMember.getUuid(), member.getUuid());
        }
    }

    @Nested
    @DisplayName("멤버의 잔액을 조회할 때")
    class 멤버의_잔액을_조회할때 {

        @Test
        @DisplayName("uuid가 전달될 때, 멤버의 잔액이 조회된다")
        void uuid가_전달될때_멤버의_잔액이_조회된다() {
            String name = "Tom Cruise";
            Member member = Member.of(name);
            member.updateBalance(100);
            UUID uuid = member.getUuid();

            given(memberRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(member));

            long balance = sut.getMemberBalance(uuid);
            assertEquals(100, balance);
        }

        @Test
        @DisplayName("uuid와 콘서트 가격이 전달될 때, 멤버의 잔액이 감소한다")
        void uuid와_콘서트_가격이_전달될때_멤버의_잔액이_감소한다() {
            String name = "Tom Cruise";
            Member member = Member.of(name);
            member.updateBalance(100);
            UUID uuid = member.getUuid();

            given(memberRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(member));

            sut.decreaseBalance(uuid, 60);
            assertEquals(member.getBalance(), 40);
        }

        @Test
        @DisplayName("uuid와 콘서트 가격이 전달될 때, 멤버의 잔액보다 콘서트 가격이 크면 Exception을 반환한다")
        void uuid와_콘서트_가격이_전달될때_멤버의_잔액보다_콘서트_가격이_크면_Exception을_반환한다() {
            String name = "Tom Cruise";
            Member member = Member.of(name);
            member.updateBalance(50000);
            UUID uuid = member.getUuid();

            given(memberRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(member));

            assertThrows(CustomException.class, () -> sut.decreaseBalance(uuid, 60000));
        }
    }
}