package com.mongs.stkpushdaraja.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CallBackRequest {
    private String merchantRequestId;
    private String checkoutRequestId;
    private int resultCode;
    private String resultDesc;
    private String mpesaReceiptNumber;
    private String transactionDate;
    private String phoneNumber;
    private String amount;
    private LocalDateTime processedAt;
}
