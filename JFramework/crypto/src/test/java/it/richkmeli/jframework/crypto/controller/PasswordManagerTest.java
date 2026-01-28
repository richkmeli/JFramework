package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Base64;

import static org.junit.Assert.*;

public class PasswordManagerTest {

    @Test
    public void hashPassword() {
        String password = "test_password";
        String hashedPassword = PasswordManager.hashPassword(password);
        assertNotNull(hashedPassword);
        assertNotEquals("", hashedPassword);

        // verify format: salt + hash
        String decodedHashedPassword = new String(Base64.getUrlDecoder().decode(hashedPassword));
        assertEquals(9 + 64, decodedHashedPassword.length());
    }

    @Test
    public void verifyPassword_newFormat() {
        String password = "test_password";
        String hashedPassword = PasswordManager.hashPassword(password);
        assertTrue(PasswordManager.verifyPassword(hashedPassword, password));
    }

    @Test
    public void verifyPassword_oldFormat() {
        String password = "test_password";
        // Manually create an old-format hash
        String salt = "000000000";
        String hash = SHA256.hash(password);
        String oldFormatHash = Base64.getUrlEncoder().encodeToString((salt + hash).getBytes(Charset.defaultCharset()));

        assertTrue(PasswordManager.verifyPassword(oldFormatHash, password));
    }

    @Test
    public void verifyPassword_wrongPassword() {
        String password = "test_password";
        String wrongPassword = "wrong_password";
        String hashedPassword = PasswordManager.hashPassword(password);
        assertFalse(PasswordManager.verifyPassword(hashedPassword, wrongPassword));
    }

    @Test
    public void verifyPassword_invalidHash() {
        String password = "test_password";
        String invalidHash = "invalid_hash";
        assertFalse(PasswordManager.verifyPassword(invalidHash, password));
    }
}
