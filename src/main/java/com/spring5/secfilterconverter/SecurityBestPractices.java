/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.secfilterconverter;

import java.security.MessageDigest;

/*
Key Takeaways:
    Password Storage: Hash passwords with salt before storing
    Data Integrity: Verify files/data haven't been tampered with
    Digital Signatures: Verify authenticity of messages/APIs
    Deduplication: Identify duplicate content efficiently
    Cache Keys: Generate unique keys for cached data

MessageDigest is fundamental for security, data integrity, and various utility functions in Java applications. Choose the right algorithm based 
    on your security requirements and performance needs.

Key Considerations:
    Key Management: Store encryption keys securely (HSM, cloud KMS, or environment variables)
    Performance: Be mindful of encryption/decryption overhead
    Compliance: Ensure your solution meets HIPAA, GDPR, or other regulatory requirements
    Testing: Always test that masked data doesn't appear in logs
    Selective Masking: Only mask what's necessary to maintain useful logs

This comprehensive approach ensures that PHI data is properly protected both at rest (encryption) and in transit through your logging systems.
 */
public class SecurityBestPractices {

    // ✅ Recommended: Use SHA-256 or higher for security
    public void useSecureAlgorithms() throws Exception {
        MessageDigest secureDigest = MessageDigest.getInstance("SHA-256");
        // or "SHA-512", "SHA3-256", "SHA3-512"
    }

    // ❌ Avoid: MD5 and SHA-1 for security purposes
    public void avoidInsecureAlgorithms() {
        // MD5 and SHA-1 are vulnerable to collisions
        // Only use for non-security purposes like cache keys
    }

    // ✅ Always use salt for passwords
    public byte[] hashWithSalt(String password, byte[] salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(salt);
        return digest.digest(password.getBytes());
    }

    // ✅ Use constant-time comparison to prevent timing attacks
    public boolean safeCompare(byte[] a, byte[] b) {
        return MessageDigest.isEqual(a, b); // This is constant-time
    }
}

/*
The most common and recommended way to encrypt data in a Spring Boot Java application, especially for sensitive information within configuration 
    files (like database credentials), is by using Jasypt (Java Simplified Encryption).
Here's why Jasypt is widely adopted and how it typically works:
    Simplifies Encryption: Jasypt provides an easy-to-use API for basic encryption and decryption, eliminating the need for developers to delve 
        deep into complex cryptographic implementations.
    Configuration File Encryption: Its primary strength lies in encrypting sensitive properties within application.properties or 
        application.yml files. This prevents hardcoding secrets in plain text, enhancing security.
    Spring Boot Integration: Jasypt offers a dedicated Spring Boot starter, making integration seamless. You can add the dependency 
        and configure it with minimal effort.
    Maven Plugin: Jasypt includes a Maven plugin that allows you to encrypt properties directly within your build process, simplifying the
        management of encrypted values.

How it Works:
    You define sensitive properties in your configuration files, often wrapped with ENC(...) or DEC(...) markers.
    Jasypt intercepts these properties during application startup.
    It uses a configured encryption key (often provided as an environment variable or system property for added security) to decrypt the 
        values before they are used by the application.

Example of Jasypt usage:
// In your application.properties or application.yml
spring.datasource.password=ENC(encryptedPasswordValueHere)

Alternative approaches for specific scenarios:
    Column-level encryption: For encrypting specific columns in a database, you might use Jasypt's StringCryptoConverter or implement 
        custom encryption logic. - see EncryptedStringConverter
    Hashicorp Vault: For managing and securing secrets in a more robust and centralized manner, especially in microservices architectures, 
        integrating with Hashicorp Vault is a powerful option.
    Java Cryptographic Extensions (JCE): For highly customized encryption needs or when implementing specific cryptographic algorithms, 
        you can directly use the JCE API.
    Client-Side Field Level Encryption (CSFLE): When working with databases like MongoDB, CSFLE offers a way to encrypt data on the client side 
        before it's sent to the database.
*/
