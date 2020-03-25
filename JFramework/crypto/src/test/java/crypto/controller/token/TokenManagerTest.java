package crypto.controller.token;

import crypto.algorithm.algorithmTestUtil;
import it.richkmeli.jframework.crypto.controller.token.TokenManager;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.junit.Test;

public class TokenManagerTest {

    @Test
    public void generate_verify_numeric_compact_salted() throws CryptoException {
        for (int i : algorithmTestUtil.plainTextLengths) {
            String value = algorithmTestUtil.genString(i);

            for (int length = 3; length < 16; length++) {
                String token = TokenManager.generateNumericCompact(value, length);
                assert TokenManager.verifyNumericCompact(token, value);
            }
        }
    }


    @Test
    public void generate_verify() throws CryptoException {
        generate_verify(false);
    }

    @Test
    public void generate_verify_salted() throws CryptoException {
        generate_verify(true);
    }

    private void generate_verify(boolean salted) throws CryptoException {
        for (int i : algorithmTestUtil.plainTextLengths) {
            String value = algorithmTestUtil.genString(i);
            String token = TokenManager.generate(value, salted);

            //System.out.println("TEST: " + token);

            assert TokenManager.verify(token, value);
        }
    }

    @Test
    public void generate_verify_temporized() throws CryptoException {
        generate_verify_temporized(false);
    }

    @Test
    public void generate_verify_temporized_salted() throws CryptoException {
        generate_verify_temporized(true);
    }

    public void generate_verify_temporized(boolean salted) throws CryptoException {
        for (int i : algorithmTestUtil.plainTextLengths) {
            String value = algorithmTestUtil.genString(i);
            for (int minOfVal = 0; minOfVal < 5; minOfVal++) {
                String token = TokenManager.generateTemporized(value, minOfVal, salted);

                //System.out.println("TEST temp: " + token);

                assert TokenManager.verifyTemporized(token, value, minOfVal);
            }
        }

        // expired token
        String token = "9f12cce3aaa6de6C75362a4E8ee74b2k70a9e84U89b9de8Y5b7faddnf5fe7c9Id9072e5xc"; /*17 nov 19 - 12:45*/
        //System.out.println("TEST temp: " + token);
        assert !TokenManager.verifyTemporized(token, "expiredToken", 1);
    }

}