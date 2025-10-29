/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

/**
 *
 * @author javau
 */
public class DataIntegrityDemo {
    public static void main(String[] args) throws Exception {
        DataIntegrityService service = new DataIntegrityService();

        String data = "Important contract data";
        String checksum = service.calculateChecksum(data.getBytes());

        System.out.println("Data checksum: " + checksum);

        // Verify later
        boolean isValid = service.verifyDataIntegrity(
                data.getBytes(), checksum);
        System.out.println("Data integrity valid: " + isValid);

        // Tamper with data
        String tamperedData = "Important contract datA"; // Changed case
        boolean isTamperedValid = service.verifyDataIntegrity(
                tamperedData.getBytes(), checksum);
        System.out.println("Tampered data valid: " + isTamperedValid); // false
    }
}
