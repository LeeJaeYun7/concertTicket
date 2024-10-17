package com.example.concert.charge.controller;

import com.example.concert.charge.dto.request.ChargeRequest;
import com.example.concert.charge.dto.response.ChargeResponse;
import com.example.concert.charge.service.ChargeFacadeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ChargeController {

    private final ChargeFacadeService chargeFacadeService;

    public ChargeController(ChargeFacadeService chargeFacadeService){
        this.chargeFacadeService = chargeFacadeService;
    }


    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> chargeBalance(@RequestBody ChargeRequest chargeRequest) throws Exception {
        UUID uuid = chargeRequest.getUuid();
        long amount = chargeRequest.getAmount();

        ChargeResponse chargeResponse = chargeFacadeService.chargeBalance(uuid, amount);
        return ResponseEntity.status(HttpStatus.OK).body(chargeResponse);
    }
}
