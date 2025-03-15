package com.mongs.stkpushdaraja.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

@Component
public class Helper {

    public static String toBase64(String value){
        byte[] bytes = value.getBytes(ISO_8859_1);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String getTimestamp(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }
}
