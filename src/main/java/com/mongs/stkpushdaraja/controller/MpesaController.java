package com.mongs.stkpushdaraja.controller;

import com.mongs.stkpushdaraja.dto.AccessTokenResponse;
import com.mongs.stkpushdaraja.dto.CallBackRequest;
import com.mongs.stkpushdaraja.dto.STKPushResponse;
import com.mongs.stkpushdaraja.service.MpesaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/payments")
@Slf4j
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

    // Tried various implementations of the callback endpoint but none of them worked to be safe be on prod for you to get the callback data //
    @PostMapping("/callback")
    public ResponseEntity<?> handleMpesaCallback(@RequestBody Map<String, Object> callbackData) {
        log.info("Received M-Pesa Callback: {}", callbackData);

        try {
            Map<String, Object> body = (Map<String, Object>) callbackData.get("Body");
            if (body != null) {
                Map<String, Object> stkCallback = (Map<String, Object>) body.get("stkCallback");
                if (stkCallback != null) {
                    CallBackRequest callBackRequest = CallBackRequest.builder()
                            .merchantRequestId((String) stkCallback.get("MerchantRequestID"))
                            .checkoutRequestId((String) stkCallback.get("CheckoutRequestID"))
                            .resultCode((Integer) stkCallback.get("ResultCode"))
                            .resultDesc((String) stkCallback.get("ResultDesc"))
                            .mpesaReceiptNumber((String) stkCallback.get("MpesaReceiptNumber"))
                            .transactionDate((String) stkCallback.get("TransactionDate"))
                            .phoneNumber((String) stkCallback.get("PhoneNumber"))
                            .amount((String) stkCallback.get("Amount"))
                            .build();

                    log.info("Processed Callback: {}", callBackRequest);

                    if (callBackRequest.getResultCode() == 0) {
                        log.info("Transaction was successful");
                    } else {
                        log.warn("Transaction failed: {}", callBackRequest.getResultDesc());
                    }
                }
            }
            return ResponseEntity.ok(callbackData);
        } catch (Exception e) {
            log.error("Error processing callback: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error processing callback");
        }
    }

}
