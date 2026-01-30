package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.util.RandomStringGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordManager {

    // salt is enabled only during login process, instead set it as false for saving passwords into DB
    // SECURITY WARNING: Storing unsalted hashes in DB (saltEnabled=false) is insecure.
    // It is recommended to always use a random salt and a slow hashing algorithm like BCrypt.
    public static String hashPassword(String password, boolean saltEnabled) {
        // salt generation
        /*Random r = new SecureRandom();
        byte[] salt = new byte[9];
        r.nextBytes(salt);*/
        String saltS = "";
        String hashedPassword = "";
        if (saltEnabled) {
            saltS = RandomStringGenerator.generateAlphanumericString(9);//new String(salt);
            hashedPassword = SHA256.hash(SHA256.hash(password) + saltS);
        } else {
            saltS = "000000000";
            hashedPassword = SHA256.hash(password);
        }

        //System.out.println("hashPassword, saltS: " + saltS + " " + saltS.length() + " | hashedPassword: " + hashedPassword + " " + hashedPassword.length());
        String out = saltS + hashedPassword;
        return Base64.getUrlEncoder().encodeToString(out.getBytes(StandardCharsets.UTF_8));
    }

    // hashedPassword = db password, hashedSaltPassword = login password
    public static boolean verifyPassword(String hashedPassword, String hashedSaltPassword) {
        String decodedHashedPassword = new String(Base64.getUrlDecoder().decode(hashedPassword), StandardCharsets.UTF_8);
        String decodedHashedSaltPassword = new String(Base64.getUrlDecoder().decode(hashedSaltPassword), StandardCharsets.UTF_8);

        // Fixed salt length of 9 characters
        int saltLength = 9;
        if (decodedHashedSaltPassword.length() < saltLength || decodedHashedPassword.length() < saltLength) {
            return false;
        }

        String salt = decodedHashedSaltPassword.substring(0, saltLength);
        String hashSP = decodedHashedSaltPassword.substring(saltLength);
        String hashP = decodedHashedPassword.substring(saltLength);

        //System.out.println("verifyPassword, saltS: " + salt + " " + salt.length() + " | hashedSaltPassword: " + hashSP + " " + hashSP.length());
        String hp = SHA256.hash(hashP + salt);

        // Constant-time comparison to prevent timing attacks
        return MessageDigest.isEqual(hashSP.toLowerCase().getBytes(StandardCharsets.UTF_8),
                hp.toLowerCase().getBytes(StandardCharsets.UTF_8));
    }
}
