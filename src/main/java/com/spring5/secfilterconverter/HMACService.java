/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class HMACService {

    public String generateHMAC(String data, String secretKey) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        hmac.init(keySpec);
        byte[] hmacBytes = hmac.doFinal(data.getBytes());
        return bytesToHex(hmacBytes);
    }

    public boolean verifyHMAC(String data, String hmac, String secretKey)
            throws Exception {
        String calculatedHmac = generateHMAC(data, secretKey);
        return MessageDigest.isEqual(
                calculatedHmac.getBytes(),
                hmac.getBytes()
        );
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
