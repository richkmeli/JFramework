package crypto.algorithm;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.junit.Test;

import static crypto.algorithm.algorithmTestUtil.*;
import static org.junit.Assert.assertEquals;

public class RC4Test {

    @Test
    public void encrypt_decrypt() {

        for (int i : plainTextLengths) {
            for (int i2 : keyLengths) {

                String plain = genString(i);
                String key = genString(i2);

                String encrypted = Crypto.encryptRC4(plain, key);
                String decrypted = null;
                try {
                    decrypted = Crypto.decryptRC4(encrypted, key);
                } catch (CryptoException e) {
                    e.printStackTrace();
                    assert false;
                }

                assertEquals(plain, decrypted);
            }
        }
    }

    @Test
    public void decryptWrongString() {
        String encrypted = "NJ12_eEyaN8cf348RQf9_w=";
        try {
            Crypto.decryptRC4(encrypted, "richktest");
            assert false;
        } catch (CryptoException e) {
            //e.printStackTrace();
            assert true;
        }
    }

        @Test
    public void encrypt_decrypt__BcCompability() {
    }


}