/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/*
The most common and recommended way to encrypt data in a Spring Boot Java application, especially for sensitive information within 
configuration files (like database credentials), is by using Jasypt (Java Simplified Encryption).

Here's why Jasypt is widely adopted and how it typically works:
Simplifies Encryption: Jasypt provides an easy-to-use API for basic encryption and decryption, eliminating the need for developers to delve 
    deep into complex cryptographic implementations.
Configuration File Encryption: Its primary strength lies in encrypting sensitive properties within application.properties or application.yml 
    files. This prevents hardcoding secrets in plain text, enhancing security.
Spring Boot Integration: Jasypt offers a dedicated Spring Boot starter, making integration seamless. You can add the dependency and configure
    it with minimal effort.
Maven Plugin: Jasypt includes a Maven plugin that allows you to encrypt properties directly within your build process, simplifying the
    management of encrypted values.
How it Works:
You define sensitive properties in your configuration files, often wrapped with ENC(...) or DEC(...) markers.
Jasypt intercepts these properties during application startup.
It uses a configured encryption key (often provided as an environment variable or system property for added security) to decrypt the values
    before they are used by the application.
Example of Jasypt usage:
Java

// In your application.properties or application.yml
spring.datasource.password=ENC(encryptedPasswordValueHere)

Alternative approaches for specific scenarios:
Column-level encryption: For encrypting specific columns in a database, you might use Jasypt's StringCryptoConverter or implement custom
    encryption logic.
Hashicorp Vault: For managing and securing secrets in a more robust and centralized manner, especially in microservices architectures,
    integrating with Hashicorp Vault is a powerful option.
Java Cryptographic Extensions (JCE): For highly customized encryption needs or when implementing specific cryptographic algorithms, you 
    can directly use the JCE API.
Client-Side Field Level Encryption (CSFLE): When working with databases like MongoDB, CSFLE offers a way to encrypt data on the client side
    before it's sent to the database.

 */
@Service
public class JasyptEncryptionService {

    @Autowired
    private StringEncryptor jasyptEncryptor;

    public String encrypt(String data) {
        if (data == null) {
            return null;
        }
        return jasyptEncryptor.encrypt(data);
    }

    public String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        return jasyptEncryptor.decrypt(encryptedData);
    }

    // For encrypting PHI fields before storing in database
    public String encryptPHI(String phiData) {
        if (phiData == null) {
            return null;
        }
        return "ENC(" + encrypt(phiData) + ")";
    }
}
