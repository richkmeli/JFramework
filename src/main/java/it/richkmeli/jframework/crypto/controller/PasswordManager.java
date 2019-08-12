package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;

import java.nio.charset.Charset;
import java.util.Base64;

public class PasswordManager {

    // salt is enabled only during login process, instead set it as false for saving passwords into DB
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
        return Base64.getUrlEncoder().encodeToString(out.getBytes(Charset.defaultCharset()));
    }

    // hashedPassword = db password, hashedSaltPassword = login password
    public static boolean verifyPassword(String hashedPassword, String hashedSaltPassword) {
        String decodedHashedPassword = new String(Base64.getUrlDecoder().decode(hashedPassword));
        String decodedHashedSaltPassword = new String(Base64.getUrlDecoder().decode(hashedSaltPassword));
        String salt = decodedHashedSaltPassword.substring(0, 9);
        String hashSP = decodedHashedSaltPassword.substring(9);
        String hashP = decodedHashedPassword.substring(9);

        //System.out.println("verifyPassword, saltS: " + salt + " " + salt.length() + " | hashedSaltPassword: " + hashSP + " " + hashSP.length());
        String hp = SHA256.hash(hashP + salt);

        return hashSP.equalsIgnoreCase(hp);
    }
}
