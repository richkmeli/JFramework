package it.richkmeli.jframework.crypto.algorithm;


import it.richkmeli.jframework.crypto.exception.CryptoException;

import java.util.Base64;

public class RC4 {
    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];
    private final int keylen;

    public RC4(final byte[] key) {
        if (key.length < 1 || key.length > 256) {
            throw new IllegalArgumentException(
                    "key must be between 1 and 256 bytes");
        } else {
            keylen = key.length;
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % keylen];
            }
            int j = 0;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xFF;
                S[i] ^= S[j];
                S[j] ^= S[i];
                S[i] ^= S[j];
            }
        }
    }

    public byte[] encrypt(final byte[] plaintext) {
        final byte[] ciphertext = new byte[plaintext.length];
        int i = 0;
        int j = 0;
        int k;
        int t;
        for (int counter = 0; counter < plaintext.length; counter++) {
            i = (i + 1) & 0xFF;
            j = (j + S[i]) & 0xFF;
            S[i] ^= S[j];
            S[j] ^= S[i];
            S[i] ^= S[j];
            t = (S[i] + S[j]) & 0xFF;
            k = S[t];
            ciphertext[counter] = (byte) (plaintext[counter] ^ k);
        }
        return ciphertext;
    }

    public byte[] decrypt(final byte[] ciphertext) {
        return encrypt(ciphertext);
    }


    public static String encrypt(String input, String key) {
        // encode input
        String inputEnc = Base64.getEncoder().encodeToString(input.getBytes());

        it.richkmeli.jframework.crypto.algorithm.RC4 rc4 = new it.richkmeli.jframework.crypto.algorithm.RC4(key.getBytes());
        byte[] ciphertext = rc4.encrypt(inputEnc.getBytes());

        // encode encrypted input
        return Base64.getUrlEncoder().encodeToString(ciphertext);
    }

    public static String decrypt(String input, String key) throws CryptoException {
        byte[] decoded;
        try {
            // decode encrypted input
            decoded = Base64.getUrlDecoder().decode(input);
        } catch (Exception e) {
            throw new CryptoException("Error decoding Base64 string: " + input, e);
        }

        it.richkmeli.jframework.crypto.algorithm.RC4 rc4 = new it.richkmeli.jframework.crypto.algorithm.RC4(key.getBytes());
        byte[] plaintext = rc4.decrypt(decoded);
        String plaintextS = new String(plaintext);

        // decode input
        try {
            decoded = Base64.getDecoder().decode(plaintextS);
        } catch (Exception e) {
            //throw new CryptoException("Error decoding Base64 string: " + plaintextS, e);
            throw new CryptoException("Error decrypting string: '" + input+"'. Key is not correct.", e);
        }
        return new String(decoded);
    }
}