package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.util.RandomStringGenerator;

import java.nio.charset.Charset;
import java.util.Base64;

public class PasswordManager {

    /**
     * Hashes a password using a randomly generated salt.
     * The salt is prepended to the hash.
     * The format is: salt + SHA256(SHA256(password) + salt)
     *
     * @param password the password to hash
     * @return Base64 encoded string of salt + hash
     */
    public static String hashPassword(String password) {
        String salt = RandomStringGenerator.generateAlphanumericString(9);
        String hashedPassword = SHA256.hash(SHA256.hash(password) + salt);

        String out = salt + hashedPassword;
        return Base64.getUrlEncoder().encodeToString(out.getBytes(Charset.defaultCharset()));
    }

    /**
     * Verifies a password against a stored hash.
     * It supports both old (unsalted) and new (salted) hash formats.
     *
     * @param hashedPassword the stored hash from the database
     * @param password       the plaintext password to verify
     * @return true if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String hashedPassword, String password) {
        String decodedHashedPassword;
        try {
            decodedHashedPassword = new String(Base64.getUrlDecoder().decode(hashedPassword));
        } catch (IllegalArgumentException e) {
            // Not a valid base64 string, so it can't be a valid hash.
            return false;
        }

        if (decodedHashedPassword.length() < 9) {
            return false; // Invalid format
        }

        String salt = decodedHashedPassword.substring(0, 9);
        String hashFromDB = decodedHashedPassword.substring(9);

        if (salt.equals("000000000")) {
            // unsalted password in DB.
            // hash is SHA256(password)
            String calculatedHash = SHA256.hash(password);
            return hashFromDB.equalsIgnoreCase(calculatedHash);
        } else {
            // salted password in DB
            // hash is SHA256(SHA256(password) + salt)
            String calculatedHash = SHA256.hash(SHA256.hash(password) + salt);
            return hashFromDB.equalsIgnoreCase(calculatedHash);
        }
    }
}
