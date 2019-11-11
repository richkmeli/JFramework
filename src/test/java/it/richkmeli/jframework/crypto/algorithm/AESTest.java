package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;

import static it.richkmeli.jframework.crypto.algorithm.algorithmTestUtil.*;
import static org.junit.Assert.assertEquals;

public class AESTest {

    @Test
    public void generateKey_encrypt_decrypt() {

        for (int i : plainTextLengths) {

            String plain = genString(i);

            SecretKey AESsecretKey = null;
            AESsecretKey = AES.generateKey(32);


            String decrypted = null;
            try {
                String encrypted = AES.encrypt(plain, AESsecretKey);
                //String encrypted2 = AES.encrypt(plain, AESsecretKey);
                //System.out.println(encrypted + " " + encrypted2);
                decrypted = AES.decrypt(encrypted, AESsecretKey);
            } catch (CryptoException e) {
                e.printStackTrace();
                assert false;
            }

            assertEquals(plain, decrypted);
        }
    }

    @Test
    public void aesString() {

        for (int i : plainTextLengths) {
            for (int i2 : keyLengths) {

                String plain = genString(i);
                String key = genString(i2);

                String decrypted = null;
                try {
                    String encrypted = AES.encrypt(plain, key);
                    decrypted = AES.decrypt(encrypted, key);
                } catch (CryptoException e) {
                    e.printStackTrace();
                    assert false;
                }

                assertEquals(plain, decrypted);
            }
        }

    }

    @Test
    public void aesGcm() {

        for (int i : plainTextLengths) {

            String plain = genString(i);

            SecretKey AESsecretKey = null;
            AESsecretKey = AES.generateKey(32);


            List<byte[]> ivs = IvManager.generateIvs(128, 5);

            for (byte[] iv : ivs) {
                String decrypted = null;
                try {
                    String encrypted = AES.encrypt(plain, AESsecretKey, iv);
                    decrypted = AES.decrypt(encrypted, AESsecretKey, iv);
                    System.out.println("plain: " + plain + " | encrypted: " + encrypted + " | decrypted: " + decrypted + " | iv: " + Base64.getEncoder().encodeToString(iv));
                    assertEquals(plain, decrypted);

                    // decrypt with a wrong iv
                    byte[] wrongIv = IvManager.generateIvs(128, 1).get(0); // "0000000000000000".getBytes();
                    String decrypted2 = "";
                    try {
                        decrypted2 = AES.decrypt(encrypted, AESsecretKey, wrongIv);
                        assert false;
                    } catch (CryptoException ce) {
                        System.out.println("plain: " + plain + " | encrypted: " + encrypted + " | decrypted: " + decrypted2 + " | wrongIv: " + Base64.getEncoder().encodeToString(wrongIv));
                    }
                    //assertNotEquals(plain, decrypted);
                } catch (CryptoException e) {
                    e.printStackTrace();
                    assert false;
                }
            }


        }
    }
}