package com.example.concert.charge.controller;

import com.example.concert.charge.dto.request.ChargeRequest;
import com.example.concert.charge.dto.response.ChargeResponse;
import com.example.concert.charge.service.ChargeFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ChargeController {

    private final ChargeFacade chargeFacade;

    public ChargeController(ChargeFacade chargeFacade){
        this.chargeFacade = chargeFacade;
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargeResponse> chargeBalance(@RequestBody ChargeRequest chargeRequest) {
        UUID uuid = chargeRequest.getUuid();
        long amount = chargeRequest.getAmount();

        long updatedBalance = chargeFacade.chargeBalance(uuid, amount);
        ChargeResponse chargeResponse = ChargeResponse.of(updatedBalance);

        return ResponseEntity.status(HttpStatus.OK).body(chargeResponse);
    }
}
