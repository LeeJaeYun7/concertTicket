package com.example.concert.member.controller;


import com.example.concert.member.dto.request.MemberRequest;
import com.example.concert.member.dto.response.MemberBalanceResponse;
import com.example.concert.member.dto.response.MemberResponse;
import com.example.concert.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService){
        this.memberService = memberService;
    }

    @PostMapping("/member")
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberRequest memberRequest){
        String name = memberRequest.getName();
        MemberResponse memberResponse = memberService.createMember(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @GetMapping("/member/uuid")
    public ResponseEntity<MemberResponse> getMemberUuid(@RequestParam(value="name") String name) throws Exception {
        MemberResponse memberResponse = memberService.getMemberUuid(name);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @GetMapping("/member/balance")
    public ResponseEntity<MemberBalanceResponse> getMemberBalance(@RequestParam(value="uuid") UUID uuid) throws Exception {
        MemberBalanceResponse memberBalanceResponse = memberService.getMemberBalance(uuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberBalanceResponse);
    }
}
