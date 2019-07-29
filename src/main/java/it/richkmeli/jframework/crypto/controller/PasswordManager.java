package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;

import java.nio.charset.Charset;
import java.util.Base64;

public class PasswordManager {

    public static String hashPassword(String password) {
        // salt generation
        /*Random r = new SecureRandom();
        byte[] salt = new byte[9];
        r.nextBytes(salt);*/
        String saltS = RandomStringGenerator.GenerateAlphanumericString(9);//new String(salt);

        String hashedPassword = SHA256.hash(SHA256.hash(password) + saltS);

        System.out.println("hashPassword, saltS: " + saltS + " " + saltS.length() + " | hashedPassword: " + hashedPassword + " " + hashedPassword.length());
        String out = saltS + hashedPassword;
        return Base64.getUrlEncoder().encodeToString(out.getBytes(Charset.defaultCharset()));
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        String decodedHashPassword = new String(Base64.getUrlDecoder().decode(hashedPassword));
        String salt = decodedHashPassword.substring(0, 9);
        String hash = decodedHashPassword.substring(9);

        System.out.println("verifyPassword, saltS: " + salt + " " + salt.length() + " | hashedPassword: " + hash + " " + hash.length());

        String hp = SHA256.hash(SHA256.hash(password) + salt);

        return hash.equalsIgnoreCase(hp);
    }
}
