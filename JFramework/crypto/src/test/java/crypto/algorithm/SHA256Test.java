package crypto.algorithm;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.util.RandomStringGenerator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SHA256Test {


    @Test
    public void hash() {
        try {
            for (int i = 1; i < 10000; i = i * 1 * 2) {
                //String a = "00000000";
                String a = RandomStringGenerator.generateAlphanumericString(i);
                String hash = SHA256.hash(a);
                System.out.println("i: " + i + " - " + a + " : " + hash);

                assertEquals(hash, SHA256.hash(a));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}