package com.example.concert.member.controller;

import com.example.concert.member.dto.request.MemberRequest;
import com.example.concert.member.dto.response.MemberBalanceResponse;
import com.example.concert.member.dto.response.MemberResponse;
import com.example.concert.member.service.MemberService;
import com.example.concert.member.vo.MemberVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/api/v1/member")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest){
        String name = memberRequest.getName();
        MemberVO memberVO = memberService.createMember(name);
        MemberResponse memberResponse = MemberResponse.of(memberVO.getUuid(), memberVO.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @GetMapping("/api/v1/member/balance")
    public ResponseEntity<MemberBalanceResponse> getMemberBalance(@RequestParam(value="uuid") String uuid) {
        long balance = memberService.getMemberBalance(uuid);
        MemberBalanceResponse memberBalanceResponse = MemberBalanceResponse.of(balance);

        return ResponseEntity.status(HttpStatus.CREATED).body(memberBalanceResponse);
    }
}
