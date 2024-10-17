package com.example.concert.member.service;

import com.example.concert.member.domain.Member;
import com.example.concert.member.dto.response.MemberBalanceResponse;
import com.example.concert.member.dto.response.MemberResponse;
import com.example.concert.member.repository.MemberRepository;
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
    public MemberResponse createMember(String name){
        Member member = Member.of(name);
        Member savedMember = memberRepository.save(member);

        UUID savedUuid = savedMember.getUuid();
        return MemberResponse.of(savedUuid);
    }

    public MemberResponse getMemberUuid(String name) throws Exception {
        Member member = memberRepository.findByName(name).orElseThrow(Exception::new);
        UUID uuid = member.getUuid();
        return MemberResponse.of(uuid);
    }

    public Member getMemberByUuid(UUID uuid) throws Exception {
        return memberRepository.findByUuid(uuid).orElseThrow(Exception::new);
    }

    public Member getMemberByUuidWithLock(UUID uuid) throws Exception {
        return memberRepository.findByUuidWithLock(uuid).orElseThrow(Exception::new);
    }

    public MemberBalanceResponse getMemberBalance(UUID uuid) throws Exception {
        long balance = getMemberByUuidWithLock(uuid).getBalance();
        return MemberBalanceResponse.of(balance);
    }

    public void decreaseBalance(UUID uuid, long price) throws Exception {
        Member member = getMemberByUuidWithLock(uuid);
        member.updateBalance(member.getBalance()-price);
    }
}
