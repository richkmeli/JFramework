package crypto.algorithm;

import it.richkmeli.jframework.util.RandomStringGenerator;


public class algorithmTestUtil {
    public static int[] plainTextLengths = {8, 10, 13, 17, 53, 100, 1000};
    public static int[] keyLengths = {8, 10, 12, 16 /*128 bits*/, 32 /*256 bits*/, 64 /*512 bits*/};

    public static String genString(int i) {
        return RandomStringGenerator.generateAlphanumericString(i);
    }


}