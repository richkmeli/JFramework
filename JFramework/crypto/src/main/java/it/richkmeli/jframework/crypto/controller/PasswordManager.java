package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.util.RandomStringGenerator;

import java.nio.charset.Charset;
import java.util.Base64;

public class PasswordManager {

    /**
     * Hashes a password using a randomly generated salt.
     * The salt is prepended to the hash and the result is Base64 encoded.
     *
     * @param password the password to hash
     * @return the hashed password with the salt, Base64 encoded
     */
    public static String hashPassword(String password) {
        String salt = RandomStringGenerator.generateAlphanumericString(16);
        String hashedPassword = SHA256.hash(password + salt);
        String out = salt + hashedPassword;
        return Base64.getUrlEncoder().encodeToString(out.getBytes(Charset.defaultCharset()));
    }

    /**
     * Verifies a plaintext password against a stored hashed password.
     *
     * @param plaintextPassword the password to verify
     * @param hashedPassword the stored hashed password (must contain the salt)
     * @return true if the password is correct, false otherwise
     */
    public static boolean verifyPassword(String plaintextPassword, String hashedPassword) {
        try {
            String decodedHashedPassword = new String(Base64.getUrlDecoder().decode(hashedPassword));
            // The salt is the first 16 characters of the decoded hash
            String salt = decodedHashedPassword.substring(0, 16);
            String storedHash = decodedHashedPassword.substring(16);
            String computedHash = SHA256.hash(plaintextPassword + salt);
            return storedHash.equals(computedHash);
        } catch (Exception e) {
            // If any error occurs (e.g., decoding, substring), verification fails
            return false;
        }
    }
}
