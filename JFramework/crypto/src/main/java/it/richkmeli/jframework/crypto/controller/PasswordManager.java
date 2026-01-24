package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.util.RandomStringGenerator;

import java.nio.charset.Charset;
import java.util.Base64;

public class PasswordManager {

    public static String hashPassword(String password) {
        String salt = RandomStringGenerator.generateAlphanumericString(9);
        String hashedPassword = SHA256.hash(password + salt);
        String out = salt + hashedPassword;
        return Base64.getUrlEncoder().encodeToString(out.getBytes(Charset.defaultCharset()));
    }

    public static boolean verifyPassword(String password, String storedHash) {
        String decodedStoredHash = new String(Base64.getUrlDecoder().decode(storedHash));
        String salt = decodedStoredHash.substring(0, 9);
        String originalHash = decodedStoredHash.substring(9);
        String loginHash = SHA256.hash(password + salt);
        return originalHash.equalsIgnoreCase(loginHash);
    }
}
