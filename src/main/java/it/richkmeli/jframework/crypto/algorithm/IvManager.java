package it.richkmeli.jframework.crypto.algorithm;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class IvManager {

    public static List<byte[]> generateIvs(int algorithmBits, int seedsNumber) {
        List<byte[]> ivs = new ArrayList<>();

        byte[] iv;
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < seedsNumber; i++) {
            iv = new byte[algorithmBits / 8];
            secureRandom.nextBytes(iv);
            ivs.add(iv);
        }

        //IvParameterSpec ivspec = new IvParameterSpec(iv);
        return ivs;
    }
}
