package com.mongs.stkpushdaraja.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongs.stkpushdaraja.dto.AccessTokenResponse;
import com.mongs.stkpushdaraja.utils.Helper;
import com.mongs.stkpushdaraja.dto.STKPushRequest;
import com.mongs.stkpushdaraja.dto.STKPushResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class MpesaService {

    @Value("${mpesa.daraja.consumer-key}")
    private String consumerKey;
    @Value("${mpesa.daraja.consumer-secret}")
    private String consumerSecret;
    @Value("${mpesa.daraja.business-shortcode}")
    private String businessShortCode;
    @Value("${mpesa.daraja.passkey}")
    private String passkey;
    @Value("${mpesa.daraja.stk-push-url}")
    private String stkPushurl;
    @Value("${mpesa.daraja.stk-push-callback-url}")
    private String stkPushCallbackUrl;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;


    public MpesaService(OkHttpClient okHttpClient, ObjectMapper objectMapper) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates access token for the M-Pesa API calls using the consumer key and secret provided by Safaricom Daraja API portal https://developer.safaricom.co.ke/
     * @return AccessTokenResponse
     * @throws IOException
     */

    public AccessTokenResponse generateAccessToken() throws IOException {
        String credentials = Credentials.basic(consumerKey, consumerSecret);
        // Prepare the request
        Request request = new Request.Builder()
                .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
                .get()
                .addHeader("Authorization", credentials)
                .build();
        log.debug("Request prepared: {}", request);
        // Execute the request
        try (Response response = okHttpClient.newCall(request).execute()) {
            log.debug("Response received: {}", response);
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                log.error("Failed to generate access token. Response: {} - {}", response.code(), responseBody);
                throw new IOException("Failed to generate access token");
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            return objectMapper.readValue(responseBody, AccessTokenResponse.class);
        } catch (IOException e) {
            log.error("Failed to generate access token.", e);
            throw e;
        }
    }

    /**
     * Initiates STK push to the user's phone number
     * @param phoneNumber
     * @param amount
     * @return STKPushResponse
     * @throws IOException
     */
    // Lets get our hands dirty by implementing the method that will initiate the STK push //
    public STKPushResponse initiateSTKPush(String phoneNumber, String amount) throws IOException {
        //format the phone number
        phoneNumber = phoneNumber.startsWith("0") ? phoneNumber.replaceFirst("0", "254") : phoneNumber;
        //generate access token
        AccessTokenResponse accessTokenResponse = generateAccessToken();
        // Prepare the request
        String timestamp = Helper.getTimestamp();
        String password = Helper.toBase64(businessShortCode + passkey + timestamp);

        STKPushRequest stkPushRequest = new STKPushRequest();
        stkPushRequest.setBusinessShortCode(businessShortCode);
        stkPushRequest.setPassword(password);
        stkPushRequest.setTimestamp(timestamp);
        stkPushRequest.setTransactionType("CustomerPayBillOnline");
        stkPushRequest.setAmount(amount);
        stkPushRequest.setPartyA(phoneNumber);
        stkPushRequest.setPartyB(businessShortCode);
        stkPushRequest.setPhoneNumber(phoneNumber);
        stkPushRequest.setCallBackURL(stkPushCallbackUrl);
        stkPushRequest.setAccountReference("Test");
        stkPushRequest.setTransactionDesc("Test");

        String jsonRequest = objectMapper.writeValueAsString(stkPushRequest);
        RequestBody requestBody = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(stkPushurl)
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + accessTokenResponse.getAccess_token())
                .addHeader("Content-Type", "application/json")
                .build();
        log.debug("Request prepared: {}", request);
        // Execute the request
        try (Response response = okHttpClient.newCall(request).execute()) {
            log.debug("Response received: {}", response);
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                log.error("Failed to initiate STK push. Response: {} - {}", response.code(), responseBody);
                throw new IOException("Failed to initiate STK push");
            }
            String responseBody = Objects.requireNonNull(response.body()).string();
            log.info("Successfully initiated STK push. Response: {}", responseBody);
            return objectMapper.readValue(responseBody, STKPushResponse.class);
        } catch (IOException e) {
            log.error("Failed to initiate STK push.", e);
            throw e;
        }
    }

}
