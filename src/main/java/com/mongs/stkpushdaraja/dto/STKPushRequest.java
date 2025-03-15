package com.mongs.stkpushdaraja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class STKPushRequest {
    @JsonProperty("BusinessShortCode")
    private String businessShortCode;
    @JsonProperty("Password")
    private String password;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("TransactionType")
    private String transactionType;
    @JsonProperty("Amount")
    private String amount;
    @JsonProperty("PartyA")
    private String partyA;
    @JsonProperty("PartyB")
    private String partyB;
    @JsonProperty("PhoneNumber")
    private String phoneNumber;
    @JsonProperty("CallBackURL")
    private String callBackURL;
    @JsonProperty("AccountReference")
    private String accountReference;
    @JsonProperty("TransactionDesc")
    private String transactionDesc;
}
