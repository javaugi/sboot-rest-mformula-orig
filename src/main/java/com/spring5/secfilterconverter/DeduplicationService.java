/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import java.security.MessageDigest;
import org.springframework.stereotype.Service;

@Service
public class DeduplicationService {

    public String generateContentId(String content) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content.getBytes());

        // Use first 16 chars for shorter ID (adjust based on collision probability needs)
        return bytesToHex(hash).substring(0, 16);
    }

    public String generateCacheKey(Object... objects) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5"); // Faster for caching

        for (Object obj : objects) {
            if (obj != null) {
                digest.update(obj.toString().getBytes());
            }
        }

        return bytesToHex(digest.digest());
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
