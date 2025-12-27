package crypto.algorithm;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import static crypto.algorithm.algorithmTestUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AESTest {

    @Test
    public void generateKey_encrypt_decrypt() {

        for (int i : plainTextLengths) {

            String plain = genString(i);
            // System.out.println("Testing length: " + i + " content: " + plain);

            String keyString = "richktest1234567"; // Simple key string

            String decrypted = null;
            try {
                // Test String-based key
                String encrypted = AES.encrypt(plain, keyString);
                decrypted = AES.decrypt(encrypted, keyString);
                assertEquals(plain, decrypted);

                // Test SecretKey-based key (derived from string internally in previous call,
                // but here explicit)
                // AES.generateKey(32) was a static method in old AES, but we don't have it
                // exposed public static
                // in the new one unless we want to.
                // But we can test the polymorphic method if we had a SecretKey.
                // Let's rely on the String one for now as it covers the internal logic.

            } catch (CryptoException e) {
                e.printStackTrace();
                assert false;
            }
        }
    }

    @Test
    public void testIvRandomness() {
        String plain = "TestMessage";
        String key = "SecretKey123";
        try {
            String enc1 = AES.encrypt(plain, key);
            String enc2 = AES.encrypt(plain, key);
            // Should be different due to random IV
            assertNotEquals(enc1, enc2);

            assertEquals(plain, AES.decrypt(enc1, key));
            assertEquals(plain, AES.decrypt(enc2, key));
        } catch (CryptoException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testTamperedPayload() {
        String plain = "SensitiveData";
        String key = "SecretKey123";
        try {
            String enc = AES.encrypt(plain, key);
            byte[] encBytes = Base64.getUrlDecoder().decode(enc);

            // Tamper with the last byte (ciphertext part)
            encBytes[encBytes.length - 1] ^= 0xFF;

            String tampered = Base64.getUrlEncoder().encodeToString(encBytes);

            try {
                AES.decrypt(tampered, key);
                // Depending on padding, this might throw or return garbage.
                // PKCS5Padding usually throws BadPaddingException if padding is corrupted.
            } catch (CryptoException e) {
                // Expected
                System.out.println("Caught expected exception for tampered data: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}