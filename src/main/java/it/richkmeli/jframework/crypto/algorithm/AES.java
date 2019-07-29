package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

public class AES {
    private static int keySize = 256;
    //private static String algorithm = "AES/CBC/PKCS5Padding";
    public static final String ALGORITHM = "AES";
    private static final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME; //"BC";


    public static SecretKey generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        ProviderManager.init(new BouncyCastleProvider());

        KeyGenerator keyGenerator = null;
        keyGenerator = KeyGenerator.getInstance(ALGORITHM, PROVIDER);
        keyGenerator.init(keySize);

        return keyGenerator.generateKey();
    }

    // entry point, key as string
    public static String encrypt(String plaintext, String key) throws CryptoException {
        byte[] decodedKey = (checkKey(key)).getBytes();
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

        return encrypt(plaintext, originalKey);
    }

    // entry point, key as string
    public static String decrypt(String ciphertext, String key) throws CryptoException {
        byte[] decodedKey = (checkKey(key)).getBytes();
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

        return decrypt(ciphertext, originalKey);
    }

    // entry point, key as SecretKey
    public static String encrypt(String input, SecretKey key) throws CryptoException {
        // encode input
        byte[] inputB = Base64.getEncoder().encode(input.getBytes());

        byte[] ciphertext = null;
        try {
            ciphertext = encrypt(inputB, key);
        } catch (Exception e) {
            throw new CryptoException(e);
        }


        // encode encrypted input
        return Base64.getUrlEncoder().encodeToString(ciphertext);
    }

    // entry point, key as SecretKey
    public static String decrypt(String input, SecretKey key) throws CryptoException {
        // decode encrypted input
        byte[] decoded = Base64.getUrlDecoder().decode(input);

        byte[] plaintext = null;
        try {
            plaintext = decrypt(decoded, key);
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        // decode input
        return new String(Base64.getDecoder().decode(plaintext));
    }


    // Java encrypt
    private static byte[] encrypt(byte[] plaintext, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        ProviderManager.init(new BouncyCastleProvider());

        Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
        //Logger.info(new String(key.getEncoded()) + " length: " + key.getEncoded().length);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(plaintext);
    }

    // Java decrypt
    private static byte[] decrypt(byte[] ciphertext, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        ProviderManager.init(new BouncyCastleProvider());

        Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(ciphertext);
    }


    private static String checkKey(String key) {
        if (key.length() < 32) {
            while (key.length() < 32) {
                key += "0";
            }
        } else if (key.length() > 32) {
            key = key.substring(0, 32);
        }
        return key;
    }

    public static SecretKey loadSecretKey(String secretKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(secretKey.getBytes()), ALGORITHM);
    }

    public static String saveSecretKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());

    }

    private static String secretKeyToBase64(SecretKey secretKey) {
        return (secretKey != null) ? (Base64.getEncoder().encodeToString(secretKey.getEncoded())) : "";
    }
}
