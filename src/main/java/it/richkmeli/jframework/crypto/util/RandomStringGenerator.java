package it.richkmeli.jframework.crypto.util;

import java.util.Random;

public class RandomStringGenerator {
    public static String generateAlphanumericString(int lenght) {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int alphabetLength = alphabet.length();

        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < lenght; ++i) {
            result.append(alphabet.charAt(random.nextInt(alphabetLength)));
        }

        return result.toString();
    }

/*
    public static String GenerateUnboundedString(int lenght) {
        byte[] array = new byte[lenght]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
*/

    public static String generateBoundedString(int targetStringLength, int leftLimit, int rightLimit) {
        //int leftLimit = 97; // letter 'a'
        //int rightLimit = 122; // letter 'z'
        //int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();

    }


}
