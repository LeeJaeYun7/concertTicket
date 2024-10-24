package com.example.concert.charge.service;

import com.example.concert.common.CustomException;
import com.example.concert.member.domain.Member;
import com.example.concert.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChargeFacade {

    private final MemberService memberService;
    private final ChargeService chargeService;

    public ChargeFacade(MemberService memberService, ChargeService chargeService){
        this.memberService = memberService;
        this.chargeService = chargeService;
    }

    @Transactional
    public long chargeBalance(String uuid, long amount) {

        validateMember(uuid);
        chargeService.getChargeByUuid(uuid);

        Member member = memberService.getMemberByUuidWithLock(uuid);
        long balance = member.getBalance();
        long updatedBalance = balance + amount;

        member.updateBalance(updatedBalance);
        chargeService.createCharge(uuid, amount);

        return updatedBalance;
    }

    public void validateMember(String uuid) {
        memberService.getMemberByUuid(uuid);
    }
}
