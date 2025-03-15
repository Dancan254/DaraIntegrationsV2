package com.mongs.stkpushdaraja.controller;

import com.mongs.stkpushdaraja.dto.AccessTokenResponse;
import com.mongs.stkpushdaraja.dto.STKPushResponse;
import com.mongs.stkpushdaraja.service.MpesaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/payments")
public class MpesaController {

    private final MpesaService mpesaService;

    public MpesaController(MpesaService mpesaService) {
        this.mpesaService = mpesaService;
    }

    //generate access token endpoint
    @GetMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> getAccessToken() {
        try {
            return ResponseEntity.ok(mpesaService.generateAccessToken());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //  stk push endpoint
    @PostMapping("/stk-push")
    public ResponseEntity<STKPushResponse> stkPush(@RequestBody Map<String, String> payload) {
        try {
            String phoneNumber = payload.get("phoneNumber");
            String amount = payload.get("amount");

            return ResponseEntity.ok(mpesaService.initiateSTKPush(phoneNumber, amount));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
