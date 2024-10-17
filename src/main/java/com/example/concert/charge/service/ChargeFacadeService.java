package com.example.concert.charge.service;

import com.example.concert.charge.dto.response.ChargeResponse;
import com.example.concert.member.domain.Member;
import com.example.concert.member.service.MemberService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChargeFacadeService {

    private final MemberService memberService;
    private final ChargeService chargeService;

    public ChargeFacadeService(MemberService memberService, ChargeService chargeService){
        this.memberService = memberService;
        this.chargeService = chargeService;
    }

    public ChargeResponse chargeBalance(UUID uuid, long amount) throws Exception {
        validateMember(uuid);

        Member member = memberService.getMemberByUuidWithLock(uuid);
        long balance = member.getBalance();
        long updatedBalance = balance + amount;
        member.updateBalance(updatedBalance);

        chargeService.createCharge(uuid, amount);

        return ChargeResponse.of(updatedBalance);
    }
    public void validateMember(UUID uuid) throws Exception {
        memberService.getMemberByUuid(uuid);
    }
}
