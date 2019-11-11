package it.richkmeli.jframework.crypto.algorithm.bc;

import it.richkmeli.jframework.crypto.exception.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

public class AES_BC {
    private static int keySize = 256;
    public static final String ALGORITHM = "AES";
    private static String ALGORITHM_CBC = "AES/CBC/NoPadding"; //PKCS5Padding
    private static String ALGORITHM_GCM = "AES/GCM/NoPadding";
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
        return encrypt(plaintext, key, null);
    }

    // entry point, key as string
    public static String decrypt(String ciphertext, String key) throws CryptoException {
        return decrypt(ciphertext, key, null);
    }

    // entry point, key as SecretKey
    public static String encrypt(String input, SecretKey key) throws CryptoException {
        return encrypt(input, key, null);
    }

    // entry point, key as SecretKey
    public static String decrypt(String input, SecretKey key) throws CryptoException {
        return decrypt(input, key, null);
    }

    // entry point, key as string
    public static String encrypt(String plaintext, String key, byte[] iv) throws CryptoException {
        byte[] decodedKey = (checkKey(key)).getBytes();
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

        return encrypt(plaintext, originalKey, iv);
    }

    // entry point, key as string
    public static String decrypt(String ciphertext, String key, byte[] iv) throws CryptoException {
        byte[] decodedKey = (checkKey(key)).getBytes();
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

        return decrypt(ciphertext, originalKey, iv);
    }

    // entry point, key as SecretKey
    public static String encrypt(String input, SecretKey key, byte[] iv) throws CryptoException {
        // encode input
        byte[] inputB = Base64.getEncoder().encode(input.getBytes());

        byte[] ciphertext = null;
        try {
            ciphertext = encrypt(inputB, key, iv);
        } catch (Exception e) {
            throw new CryptoException(e);
        }


        // encode encrypted input
        return Base64.getUrlEncoder().encodeToString(ciphertext);
    }

    // entry point, key as SecretKey
    public static String decrypt(String input, SecretKey key, byte[] iv) throws CryptoException {
        // decode encrypted input
        byte[] decoded = Base64.getUrlDecoder().decode(input);

        byte[] plaintext = null;
        try {
            plaintext = decrypt(decoded, key, iv);
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        // decode input
        return new String(Base64.getDecoder().decode(plaintext));
    }


    // Java encrypt
    private static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException {
        ProviderManager.init(new BouncyCastleProvider());

        Cipher cipher;
        //Logger.info(new String(key.getEncoded()) + " length: " + key.getEncoded().length);
        if (iv == null) {
            cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            cipher = Cipher.getInstance(ALGORITHM_GCM, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(iv.length * 8, iv));
        }
        return cipher.doFinal(plaintext);
    }

    // Java decrypt
    private static byte[] decrypt(byte[] ciphertext, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException {
        ProviderManager.init(new BouncyCastleProvider());

        Cipher cipher;
        if (iv == null) {
            cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            cipher = Cipher.getInstance(ALGORITHM_GCM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(iv.length * 8, iv));
        }
        return cipher.doFinal(ciphertext);
    }

    // Java encrypt CBC
    private static byte[] encryptCBC(byte[] plaintext, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException, CryptoException {
        ProviderManager.init(new BouncyCastleProvider());

        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return process(cipher, plaintext);
    }

    // Java decrypt CBC
    private static byte[] decryptCBC(byte[] ciphertext, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException {
        ProviderManager.init(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return process(cipher, ciphertext);
    }

    // for CBC
    static private byte[] process(Cipher ci, byte[] inB) throws IllegalBlockSizeException, BadPaddingException, IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(inB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
            if (obuf != null) out.write(obuf);
        }
        byte[] obuf = ci.doFinal();
        if (obuf != null) out.write(obuf);

        return out.toByteArray();
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
