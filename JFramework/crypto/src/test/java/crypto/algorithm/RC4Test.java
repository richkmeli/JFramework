package crypto.algorithm;

import it.richkmeli.jframework.crypto.Crypto;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.junit.Test;

import static crypto.algorithm.algorithmTestUtil.*;
import static org.junit.Assert.assertEquals;

public class RC4Test {

    @Test(expected = CryptoException.class)
    public void encryptRC4ShouldFail() throws CryptoException {
        String plain = genString(10);
        String key = genString(10);

        // this should fail, as RC4 is insecure
        Crypto.encryptRC4(plain, key);
    }

    @Test(expected = CryptoException.class)
    public void decryptRC4ShouldFail() throws CryptoException {
        String encrypted = "NJ12_eEyaN8cf348RQf9_w=";
        // this should fail, as RC4 is insecure
        Crypto.decryptRC4(encrypted, "richktest");
    }


}