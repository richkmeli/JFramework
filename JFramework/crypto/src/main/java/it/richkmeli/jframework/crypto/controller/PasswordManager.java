package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordManager {

    private static final int SALT_LENGTH = 16; // 128 bit salt
    private static final String DELIMITER = ":";

    /**
     * Hashes a password using a securely generated salt (PBKDF2-like pattern).
     * The salt is combined with the hash for storage. This method is now the default and only way to hash passwords.
     * The insecure option to disable salting has been removed.
     *
     * @param password the plaintext password to hash.
     * @return a Base64Url encoded string containing the salt and hash, separated by a colon.
     */
    public static String hashPassword(String password) {
        // 1. Generate a cryptographically secure salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        String saltString = Base64.getUrlEncoder().encodeToString(salt);

        // 2. Hash the password with the salt (maintaining original convoluted hash for compatibility)
        String hashedPassword = SHA256.hash(SHA256.hash(password) + saltString);

        // 3. Combine salt and hash, then encode for storage
        String out = saltString + DELIMITER + hashedPassword;
        return Base64.getUrlEncoder().encodeToString(out.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifies a plaintext password against a stored hash from the database.
     * This method provides backward compatibility by checking both the new secure format and the old insecure format.
     * This allows for a "lazy migration" of password hashes.
     *
     * @param password   the plaintext password from the user login attempt.
     * @param storedHash the hash combination retrieved from the database (can be old or new format).
     * @return true if the password matches the hash, false otherwise.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        // 1. First, try to verify using the NEW, secure format (salt:hash)
        try {
            String decodedStoredHash = new String(Base64.getUrlDecoder().decode(storedHash), StandardCharsets.UTF_8);
            String[] parts = decodedStoredHash.split(DELIMITER);
            if (parts.length == 2) {
                // This appears to be the new format.
                String saltString = parts[0];
                String originalHash = parts[1];
                String computedHash = SHA256.hash(SHA256.hash(password) + saltString);
                // Securely compare the hashes to prevent timing attacks
                return MessageDigest.isEqual(originalHash.getBytes(StandardCharsets.UTF_8), computedHash.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            // Ignore exception, as it may be the old format. Fall through to the old check.
        }

        // 2. If the new format fails, try to verify using the OLD, insecure format (000000000 + hash)
        try {
            String decodedStoredHash = new String(Base64.getUrlDecoder().decode(storedHash), StandardCharsets.UTF_8);
            // The old format used a fixed, fake salt of "000000000" and a single SHA256 hash.
            if (decodedStoredHash.startsWith("000000000")) {
                String originalHash = decodedStoredHash.substring(9);
                String computedHash = SHA256.hash(password);
                // If this matches, the password is correct under the old scheme.
                // It should be re-hashed and updated by the calling service.
                return originalHash.equals(computedHash);
            }
        } catch (Exception e) {
            // If any error occurs during the old format check, fail securely.
            return false;
        }

        // 3. If neither format matches, the password is incorrect.
        return false;
    }
}
