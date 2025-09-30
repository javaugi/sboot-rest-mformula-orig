/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.pharmacy;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class GCPSecretManagerService {

    public String getGCPSecret() throws IOException {
        String secret
                = SecretManagerServiceClient.create()
                        .accessSecretVersion("projects/cvs-project/secrets/db-password/versions/latest")
                        .getPayload()
                        .getData()
                        .toStringUtf8();

        return secret;
    }
}
