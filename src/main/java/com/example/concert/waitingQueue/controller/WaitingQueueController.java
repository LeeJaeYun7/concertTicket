package com.example.concert.waitingQueue.controller;

import com.example.concert.waitingQueue.dto.request.TokenRequest;
import com.example.concert.waitingQueue.dto.response.WaitingNumberResponse;
import com.example.concert.waitingQueue.dto.response.TokenResponse;
import com.example.concert.waitingQueue.service.WaitingQueueFacade;
import com.example.concert.waitingQueue.service.WaitingQueueService;
import com.example.concert.waitingQueue.vo.TokenVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class WaitingQueueController {

    private final WaitingQueueFacade waitingQueueFacade;
    private final WaitingQueueService waitingQueueService;

    public WaitingQueueController(WaitingQueueFacade waitingQueueFacade, WaitingQueueService waitingQueueService){
        this.waitingQueueFacade = waitingQueueFacade;
        this.waitingQueueService = waitingQueueService;
    }

    @PostMapping("/waitingQueue/concert/token")
    public ResponseEntity<TokenResponse> createConcertToken(@RequestBody TokenRequest tokenRequest) {
        long concertId = tokenRequest.getConcertId();
        String uuid = tokenRequest.getUuid();

        TokenVO tokenVO = waitingQueueFacade.createConcertToken(concertId, uuid);
        TokenResponse tokenResponse = TokenResponse.of(tokenVO.getWaitingNumber());

        return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse);
    }

    @GetMapping("/waitingQueue/waitingNumber")
    public ResponseEntity<WaitingNumberResponse> retrieveWaitingNumber(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        long waitingNumber = waitingQueueService.getWaitingNumber(token);
        WaitingNumberResponse waitingNumberResponse = WaitingNumberResponse.of(waitingNumber);

        return ResponseEntity.status(HttpStatus.OK).body(waitingNumberResponse);
    }
}
