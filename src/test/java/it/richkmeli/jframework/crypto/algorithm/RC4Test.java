package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.Crypto;
import org.junit.Test;

import static it.richkmeli.jframework.crypto.algorithm.algorithmTestUtil.*;
import static org.junit.Assert.assertEquals;

public class RC4Test {

    @Test
    public void encrypt_decrypt() {

        for (int i : plainTextLengths) {
            for (int i2 : keyLengths) {

                String plain = genString(i);
                String key = genString(i2);

                String encrypted = Crypto.encryptRC4(plain, key);
                String decrypted = Crypto.decryptRC4(encrypted, key);

                assertEquals(plain, decrypted);
            }
        }
    }

    @Test
    public void encrypt_decrypt__BcCompability() {
    }


}