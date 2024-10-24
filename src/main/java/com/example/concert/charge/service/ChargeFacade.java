package com.example.concert.charge.service;

import com.example.concert.member.domain.Member;
import com.example.concert.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChargeFacade {

    private final MemberService memberService;
    private final ChargeService chargeService;

    public ChargeFacade(MemberService memberService, ChargeService chargeService){
        this.memberService = memberService;
        this.chargeService = chargeService;
    }

    public long chargeBalance(UUID uuid, long amount) {
        validateMember(uuid);

        Member member = memberService.getMemberByUuidWithLock(uuid);
        long balance = member.getBalance();
        long updatedBalance = balance + amount;
        member.updateBalance(updatedBalance);

        chargeService.createCharge(uuid, amount);

        return updatedBalance;
    }
    public void validateMember(UUID uuid) {
        memberService.getMemberByUuid(uuid);
    }
}
