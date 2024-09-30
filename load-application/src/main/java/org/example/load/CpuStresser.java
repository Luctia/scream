package org.example.load;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

@RestController
public class CpuStresser {
    // Constants for GCM
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 128; // 128 bits authentication tag

    @GetMapping("/encrypt")
    public String encrypt() throws Exception {
        String plaintext = "The quick brown fox jumps over the lazy dog";
        KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyGen.init(256); // Using 256-bit AES encryption
        SecretKey secretKey = keyGen.generateKey();

        // Generate a random IV (Initialization Vector)
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        // Initialize Cipher for Encryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // Encrypt the data
        for (int i = 0; i < 200; i++) {
            cipher.init(Cipher.ENCRYPT_MODE, keyGen.generateKey(), gcmParameterSpec);
            encryptedBytes = cipher.doFinal(encryptedBytes);
        }

        return "Done!";
    }

    @GetMapping("/fillup")
    public String fillup() throws Exception {
        Vector v = new Vector();
        byte b[] = new byte[1024 * 512];
        v.add(b);
        return "Done!";
    }

    @GetMapping("/both")
    public String both() throws Exception {
        encrypt();
        fillup();
        return "Done!";
    }
}
