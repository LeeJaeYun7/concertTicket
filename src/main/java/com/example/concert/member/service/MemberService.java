package com.example.concert.member.service;

import com.example.concert.common.CustomException;
import com.example.concert.common.ErrorCode;
import com.example.concert.member.domain.Member;
import com.example.concert.member.repository.MemberRepository;
import com.example.concert.member.vo.MemberVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberVO createMember(String name){
        Member member = Member.of(name);
        Member savedMember = memberRepository.save(member);

        return MemberVO.of(savedMember.getUuid(), savedMember.getName());
    }

    public MemberVO getMemberUuid(String name) {
        Member member = memberRepository.findByName(name)
                                        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberVO.of(member.getUuid(), member.getName());
    }

    public Member getMemberByUuid(UUID uuid) {
        return memberRepository.findByUuid(uuid)
                               .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getMemberByUuidWithLock(UUID uuid) {
        return memberRepository.findByUuidWithLock(uuid)
                               .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    public long getMemberBalance(UUID uuid) {
        return getMemberByUuidWithLock(uuid).getBalance();
    }

    public void decreaseBalance(UUID uuid, long price) {
        Member member = getMemberByUuidWithLock(uuid);
        member.updateBalance(member.getBalance()-price);
    }
}
