package it.richkmeli.jframework.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class RandomStringGenerator {
    public static final String ALPHANUMERIC_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String NUMERIC_ALPHABET = "0123456789";

    public static String generateAlphanumericString(int length) {
        String alphabet = ALPHANUMERIC_ALPHABET;
        return generateString(length, alphabet);
    }

    public static String generateNumericString(int length) {
        String alphabet = NUMERIC_ALPHABET;
        return generateString(length, alphabet);
    }

    private static String generateString(int length, String alphabet) {
        int alphabetLength = alphabet.length();

        StringBuilder result = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; ++i) {
            result.append(alphabet.charAt(secureRandom.nextInt(alphabetLength)));
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
        SecureRandom random = new SecureRandom();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();

    }

    public static String generateUtf8String(int length) {
        byte[] array = new byte[length]; // length is bounded by 7
        new SecureRandom().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    public static String generateUtf16String(int length) {
        byte[] array = new byte[length]; // length is bounded by 7
        new SecureRandom().nextBytes(array);
        return new String(array, StandardCharsets.UTF_16);
    }

    public static String generateASCIItring(int length) {
        byte[] array = new byte[length]; // length is bounded by 7
        new SecureRandom().nextBytes(array);
        return new String(array, StandardCharsets.US_ASCII);
    }


}
