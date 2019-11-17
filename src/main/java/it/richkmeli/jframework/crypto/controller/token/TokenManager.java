package it.richkmeli.jframework.crypto.controller.token;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenManager {

    public static String generate(String value) {
        return generateTemporized(value, 0);
    }


    public static String generateTemporized(String value, int minutesOfValidity) {
        // eg: abcdefgh
        String salt = RandomStringGenerator.generateAlphanumericString(9);

        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = currentTimeMillis / 1000;
        long currentTimeMinutes = currentTimeSeconds / 60;
        //System.out.println("gen: " + salt + " " + value + " " + (currentTimeMinutes * minutesOfValidity));
        // eg: 0000000000000000000000000000000000000000000000000000000000000000
        String hashed = SHA256.hash(salt + value + (currentTimeMinutes * minutesOfValidity));

        int interval = ((hashed.length() + salt.length()) / salt.length());
        //System.out.println("interval: " + interval);

        String out = insertSaltIntoHashed(salt, hashed, interval);

        // eg: 0a00000000b00000000c00000000d00000000e00000000f00000000g00000000h0000000
        return out;
    }

    public static boolean verify(String token, String value) {
        return verifyTemporized(token, value, 0);
    }

    // minutesOfValidity has to be aligned to the value set in generateTemporized
    public static boolean verifyTemporized(String token, String value, int minutesOfValidity) {
        int calculatedInterval = token.length() / (token.length() - 64);
        //System.out.println("calculatedInterval: " + calculatedInterval);

        List<String> list = extractSaltHashedFromToken(token, 8);
        String salt = list.get(0);
        String hashed = list.get(1);
        //System.out.println("hashed: " + hashed);

        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = currentTimeMillis / 1000;
        long currentTimeMinutes = currentTimeSeconds / 60;

        //System.out.println("ver: " + salt + " " + value + " " + (currentTimeMinutes * minutesOfValidity));
        String calculatedHash = SHA256.hash(salt + value + (currentTimeMinutes * minutesOfValidity));
        //System.out.println("calculatedHash: " + calculatedHash);

        String calculatedHashWithSalt = insertSaltIntoHashed(salt, calculatedHash, 8);
        //System.out.println("calculatedHashWithSalt: " + calculatedHashWithSalt);

        if (hashed.equalsIgnoreCase(calculatedHash)) {
            return true;
        } else {
            return false;
        }
    }


    private static String insertSaltIntoHashed(String salt, String hashed, int interval) {
        StringBuilder out = new StringBuilder();
        for (int i = 0, iS = 0; i < hashed.length(); ++i) {
            if (((i + iS) % interval) == (interval - 1)) {
                // [0,1,2,3]
                out.append(salt.charAt((i + iS) / interval));
                iS++;
            }
            out.append(hashed.charAt(i));

        }
        return out.toString();
    }

    private static List<String> extractSaltHashedFromToken(String token, int interval) {
        StringBuilder salt = new StringBuilder();
        StringBuilder hashed = new StringBuilder();
        for (int i = 0; i < token.length(); ++i) {
            if ((i % interval) == (interval - 1)) {
                salt.append(token.charAt(i));
                //System.out.println("s" + i + ": " + token.charAt(i));
            } else {
                hashed.append(token.charAt(i));
                //System.out.println("h" + i + ": " + token.charAt(i));
            }
        }
        return new ArrayList<>(Arrays.asList(salt.toString(), hashed.toString()));
    }
}
