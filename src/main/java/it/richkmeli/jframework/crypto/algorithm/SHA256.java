package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.util.TypeConverter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {

    public static final String ALGORITHM = "SHA-256";

    // sha256: string to hex
    public static String hash(String input) {
        // encode input
        //input = DatatypeConverter.printBase64Binary(input.getBytes());
        //input = DatatypeConverter.printHexBinary(input.getBytes());

        MessageDigest digest = null;
        byte[] hash = null;
        try {
            digest = MessageDigest.getInstance(ALGORITHM);
            hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // encode encrypted input
        //return DatatypeConverter.printBase64Binary(ciphertext);
        //return Base64.getUrlEncoder().encodeToString(hash);
        // to hex
        return TypeConverter.bytesToHex(hash);
    }
}
