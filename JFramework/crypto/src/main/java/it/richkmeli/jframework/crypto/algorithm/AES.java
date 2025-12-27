package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AES {
    public static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static String encrypt(String input, String key) throws CryptoException {
        try {
            return encrypt(input, generateKey(key));
        } catch (Exception e) {
            throw new CryptoException("Error generating key from string", e);
        }
    }

    public static String encrypt(String input, SecretKeySpec key) throws CryptoException {
        try {
            // Encode input to ensure consistency
            String inputEnc = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] iv = new byte[cipher.getBlockSize()];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
            byte[] ciphertext = cipher.doFinal(inputEnc.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to ciphertext
            byte[] ivAndCiphertext = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, iv.length);
            System.arraycopy(ciphertext, 0, ivAndCiphertext, iv.length, ciphertext.length);

            return Base64.getUrlEncoder().encodeToString(ivAndCiphertext);
        } catch (Exception e) {
            throw new CryptoException("Error encrypting with AES", e);
        }
    }

    // For compatibility with javax.crypto.SecretKey interface
    public static String encrypt(String input, javax.crypto.SecretKey key) throws CryptoException {
        if (key instanceof SecretKeySpec) {
            return encrypt(input, (SecretKeySpec) key);
        } else {
            return encrypt(input, new SecretKeySpec(key.getEncoded(), ALGORITHM));
        }
    }

    public static String decrypt(String input, String key) throws CryptoException {
        try {
            return decrypt(input, generateKey(key));
        } catch (Exception e) {
            throw new CryptoException("Error generating key from string", e);
        }
    }

    public static String decrypt(String input, SecretKeySpec key) throws CryptoException {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(input);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            int blockSize = cipher.getBlockSize();
            if (decoded.length < blockSize) {
                throw new CryptoException("Error decrypting: input too short");
            }

            // Extract IV
            byte[] iv = Arrays.copyOfRange(decoded, 0, blockSize);
            IvParameterSpec ivParams = new IvParameterSpec(iv);

            // Extract Ciphertext
            byte[] ciphertext = Arrays.copyOfRange(decoded, blockSize, decoded.length);

            cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
            byte[] plaintextBytes = cipher.doFinal(ciphertext);

            String plaintextEnc = new String(plaintextBytes, StandardCharsets.UTF_8);

            // Decode inner Base64
            byte[] originalBytes = Base64.getDecoder().decode(plaintextEnc);
            return new String(originalBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new CryptoException("Error decrypting with AES. Key might be incorrect.", e);
        }
    }

    // For compatibility with javax.crypto.SecretKey interface
    public static String decrypt(String input, javax.crypto.SecretKey key) throws CryptoException {
        if (key instanceof SecretKeySpec) {
            return decrypt(input, (SecretKeySpec) key);
        } else {
            return decrypt(input, new SecretKeySpec(key.getEncoded(), ALGORITHM));
        }
    }

    private static SecretKeySpec generateKey(String key) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        keyBytes = sha.digest(keyBytes);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public static javax.crypto.SecretKey generateKey(byte[] keyBytes, int length) {
        try {
            // We can use SHA-256 to ensure the key is always 32 bytes (256 bits)
            // regardless of input length, which is safer than truncation/padding.
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = sha.digest(keyBytes);
            // If length is 32 (256 bits), we are good. If 16 (128 bits), we take first 16.
            if (length == 32) {
                return new SecretKeySpec(hashedKey, ALGORITHM);
            } else if (length == 16) {
                return new SecretKeySpec(Arrays.copyOf(hashedKey, 16), ALGORITHM);
            } else {
                // Fallback or error? Let's default to full SHA-256 which is 32 bytes
                return new SecretKeySpec(hashedKey, ALGORITHM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveSecretKey(javax.crypto.SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static javax.crypto.SecretKey loadSecretKey(String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }
}
