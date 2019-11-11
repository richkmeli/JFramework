package it.richkmeli.jframework.crypto.algorithm;

import org.junit.Test;

import java.util.Base64;
import java.util.List;

public class IvManagerTest {

    @Test
    public void generateIvs() {
        try {
            List<byte[]> ivs = IvManager.generateIvs(256, 20);

            for (byte[] b : ivs) {
                System.out.println(Base64.getEncoder().encodeToString(b) + " " + new String(b));
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

}