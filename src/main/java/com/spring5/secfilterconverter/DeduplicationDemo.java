/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

public class DeduplicationDemo {

    public static void main(String[] args) throws Exception {
        DeduplicationService service = new DeduplicationService();

        String content1 = "This is some content";
        String content2 = "This is some content"; // Same as content1
        String content3 = "This is different content";

        String id1 = service.generateContentId(content1);
        String id2 = service.generateContentId(content2);
        String id3 = service.generateContentId(content3);

        System.out.println("ID 1: " + id1);
        System.out.println("ID 2: " + id2);
        System.out.println("ID 3: " + id3);
        System.out.println("ID1 equals ID2: " + id1.equals(id2)); // true
        System.out.println("ID1 equals ID3: " + id1.equals(id3)); // false

        // Cache key generation
        String cacheKey = service.generateCacheKey("user", 12345, "profile");
        System.out.println("Cache key: " + cacheKey);
    }
}
