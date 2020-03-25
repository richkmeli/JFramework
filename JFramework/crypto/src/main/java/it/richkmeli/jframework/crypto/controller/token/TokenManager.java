package it.richkmeli.jframework.crypto.controller.token;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenManager {

    public static String generateNumericCompact(String value, int length) {
        // generate a numeric salf of 2 number
        String numericSalt = RandomStringGenerator.generateNumericString(2);
        String extendedToken = generate(numericSalt + value, false);
        // put numericSalt at first chars of extendedToken, it is always preserved by the following code because it's numeric
        extendedToken = numericSalt + extendedToken;

        return reduceToNumeric(extendedToken, length);
    }

    public static boolean verifyNumericCompact(String numericCompactToken, String value) throws CryptoException {
        if (numericCompactToken.length() <= 2) {
          throw new CryptoException("Token length not correct. Actual: " + numericCompactToken.length() + " Expected: at least 3");
        }

        String extractedNumericSalt = numericCompactToken.substring(0, 2);
         String extendedToken = generate(extractedNumericSalt + value, false);
        // put extractedNumericSalt at first chars of extendedToken
        extendedToken = reduceToNumeric(extractedNumericSalt + extendedToken, numericCompactToken.length());
        return numericCompactToken.equalsIgnoreCase(extendedToken);
    }

    public static String generate(String value) {
        return generateTemporized(value, 0, true);
    }

    public static String generate(String value, boolean salted) {
        return generateTemporized(value, 0, salted);
    }

    public static String generateTemporized(String value, int minutesOfValidity) {
        return generateTemporized(value, minutesOfValidity, true);
    }

    public static String generateTemporized(String value, int minutesOfValidity, boolean salted) {
        String out;
        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = currentTimeMillis / 1000;
        long currentTimeMinutes = currentTimeSeconds / 60;
        if (salted) {
            // eg: abcdefgh
            String salt = RandomStringGenerator.generateAlphanumericString(9);
            //System.out.println("gen: " + salt + " " + value + " " + (currentTimeMinutes * minutesOfValidity));
            // eg: 0000000000000000000000000000000000000000000000000000000000000000
            String hashed = SHA256.hash(salt + value + (currentTimeMinutes * minutesOfValidity));
            //System.out.println("hashed: " + hashed);

            int interval = ((hashed.length() + salt.length()) / salt.length());
            //System.out.println("interval: " + interval);

            // eg: 0a00000000b00000000c00000000d00000000e00000000f00000000g00000000h0000000
            out = insertSaltIntoHashed(salt, hashed, interval);
        } else {
            out = SHA256.hash(value + (currentTimeMinutes * minutesOfValidity));
        }

        return out;
    }

    public static boolean verify(String token, String value) throws CryptoException {
        return verifyTemporized(token, value, 0);
    }

    // minutesOfValidity has to be aligned to the value set in generateTemporized
    public static boolean verifyTemporized(String token, String value, int minutesOfValidity) throws CryptoException {
        boolean salted;
        if (token.length() == 64) {
            salted = false;
        } else if (token.length() == 64 + 9) {
            salted = true;
        } else {
            throw new CryptoException("Token length not correct. Actual: " + token.length() + " Expected: 64 or 73");
        }

        long currentTimeMillis = System.currentTimeMillis();
        long currentTimeSeconds = currentTimeMillis / 1000;
        long currentTimeMinutes = currentTimeSeconds / 60;

        String hashed;
        String calculatedHash;
        if (salted) {
            int calculatedInterval = token.length() / (token.length() - 64);
            //System.out.println("calculatedInterval: " + calculatedInterval);

            List<String> list = extractSaltHashedFromToken(token, 8);
            String salt = list.get(0);
            hashed = list.get(1);
            //System.out.println("hashed: " + hashed);

            //System.out.println("ver: " + salt + " " + value + " " + (currentTimeMinutes * minutesOfValidity));
            calculatedHash = SHA256.hash(salt + value + (currentTimeMinutes * minutesOfValidity));
            //System.out.println("calculatedHash: " + calculatedHash);

            String calculatedHashWithSalt = insertSaltIntoHashed(salt, calculatedHash, 8);
            //System.out.println("calculatedHashWithSalt: " + calculatedHashWithSalt);
        } else {
            hashed = token;
            calculatedHash = SHA256.hash(value + (currentTimeMinutes * minutesOfValidity));
        }
        return hashed.equalsIgnoreCase(calculatedHash);
    }

    private static String reduceToNumeric(String extendedToken, int length) {
        String numericCompactToken = extendedToken.replaceAll("\\D+", "");

        if (numericCompactToken.length() < length) {
            StringBuilder tokenBuilder = new StringBuilder(numericCompactToken);
            while (tokenBuilder.length() < length) {
                tokenBuilder.append(numericCompactToken);
            }
            // it is appended numericCompactToken, that it could contains more chars, so it is necessary to trim at the length
            numericCompactToken = tokenBuilder.toString().substring(0, length);
        } else if (numericCompactToken.length() > length) {
            numericCompactToken = numericCompactToken.substring(0, length);
        }
        return numericCompactToken;
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
