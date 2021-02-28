package it.richkmeli.jframework.crypto.algorithm;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import it.richkmeli.jframework.crypto.exception.CryptoException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AES {
    public static final String ALGORITHM = "AES";


    public static SecretKey generateKey(int keySize) {
        byte[] array = new byte[keySize]; // length is bounded by 7
        new SecureRandom().nextBytes(array);
        SecretKey secretKey = new SecretKeySpec(array, "AES");
        return secretKey;
    }

    public static SecretKey generateKey(byte[] bytes, int keySize) {
        byte[] array = Arrays.copyOf(bytes, keySize);
        return new SecretKeySpec(array, "AES");
    }

    // entry point, key as string, without IV
    public static String encrypt(String plaintext, String key) throws CryptoException {
        return encrypt(plaintext, key, null);
    }

    // entry point, key as string, without IV
    public static String decrypt(String ciphertext, String key) throws CryptoException {
        return decrypt(ciphertext, key, null);
    }

    // entry point, key as SecretKey, without IV
    public static String encrypt(String input, SecretKey key) throws CryptoException {
        return encrypt(input, key, null);
    }

    // entry point, key as SecretKey, without IV
    public static String decrypt(String input, SecretKey key) throws CryptoException {
        return decrypt(input, key, null);
    }

    // entry point, key as string, with IV
    public static String encrypt(String plaintext, String key, byte[] iv) throws CryptoException {
        byte[] decodedKey = (checkKey(key)).getBytes();
        // rebuild key using SecretKeySpec
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);

        return encrypt(plaintext, originalKey, iv);
    }

    // entry point, key as string, with IV
    public static String decrypt(String ciphertext, String key, byte[] iv) throws CryptoException {
        if(key == null){
            throw new CryptoException("key is null");
        }
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
            ciphertext = encrypt(inputB, key.getEncoded(), iv);
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
            plaintext = decrypt(decoded, key.getEncoded(), iv);
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        // decode input
        return new String(Base64.getDecoder().decode(plaintext));
    }


    public static SecretKey loadSecretKey(String secretKey) {
        return new SecretKeySpec(Base64.getDecoder().decode(secretKey.getBytes()), ALGORITHM);
    }

    public static String saveSecretKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());

    }

    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) throws CryptoException {
        return doPhase(plaintext, key, iv, true);
    }

    public static byte[] decrypt(byte[] plaintext, byte[] key, byte[] iv) throws CryptoException {
        return doPhase(plaintext, key, iv, false);
    }

    private static byte[] doPhase(byte[] plaintext, byte[] key, byte[] iv, boolean forEncryption) throws CryptoException {
        // key and IV
        KeyParameter keyParam = new KeyParameter(key);
        // backward compatibility
        if (iv == null) {
            iv = "00000000".getBytes();
        }
        CipherParameters cipherParameters = new AEADParameters(keyParam, iv.length * 8, iv);
        //new ParametersWithIV(keyParam, iv);
        // padding
        ZeroBytePadding padding = new ZeroBytePadding();

        // cipher
        GCMBlockCipher cipher = new GCMBlockCipher(new AESEngine());

        cipher.reset();
        cipher.init(forEncryption, cipherParameters);

        byte[] buffer = new byte[cipher.getOutputSize(plaintext.length)];
        int len = cipher.processBytes(plaintext, 0, plaintext.length, buffer, 0);
        try {
            len += cipher.doFinal(buffer, len);
        } catch (InvalidCipherTextException e) {
            throw new CryptoException(e);
        }
        return Arrays.copyOfRange(buffer, 0, len);
    }


    private static String checkKey(String key) {
        if (key.length() < 32) {
            StringBuilder keyBuilder = new StringBuilder(key);
            while (keyBuilder.length() < 32) {
                keyBuilder.append("0");
            }
            key = keyBuilder.toString();
        } else if (key.length() > 32) {
            key = key.substring(0, 32);
        }
        return key;
    }


    private static String secretKeyToBase64(SecretKey secretKey) {
        return (secretKey != null) ? (Base64.getEncoder().encodeToString(secretKey.getEncoded())) : "";
    }


}


//    // Java encrypt CBC
//    private static byte[] encryptCBC(byte[] plaintext, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException, CryptoException {
//        ProviderManager.init(PROVIDER);
//
//        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC, PROVIDER);
//        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
//        return process(cipher, plaintext);
//    }
//
//    // Java decrypt CBC
//    private static byte[] decryptCBC(byte[] ciphertext, SecretKey key, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, IOException, InvalidAlgorithmParameterException {
//        ProviderManager.init(PROVIDER);
//        Cipher cipher = Cipher.getInstance(ALGORITHM_CBC, PROVIDER);
//        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
//        return process(cipher, ciphertext);
//    }
//
//    // for CBC
//    static private byte[] process(Cipher ci, byte[] inB) throws IllegalBlockSizeException, BadPaddingException, IOException {
//        ByteArrayInputStream in = new ByteArrayInputStream(inB);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        byte[] ibuf = new byte[1024];
//        int len;
//        while ((len = in.read(ibuf)) != -1) {
//            byte[] obuf = ci.update(ibuf, 0, len);
//            if (obuf != null) out.write(obuf);
//        }
//        byte[] obuf = ci.doFinal();
//        if (obuf != null) out.write(obuf);
//
//        return out.toByteArray();
//    }


//    private byte[] decryptWithLWCrypto(byte[] cipher, String password, byte[] salt, final int iterationCount)
//            throws Exception {
//        PKCS12ParametersGenerator pGen = new PKCS12ParametersGenerator(new SHA256Digest());
//        char[] passwordChars = password.toCharArray();
//        final byte[] pkcs12PasswordBytes = PBEParametersGenerator
//                .PKCS12PasswordToBytes(passwordChars);
//        pGen.init(pkcs12PasswordBytes, salt, iterationCount);
//
//        CBCBlockCipher aesCBC = new CBCBlockCipher(new AESEngine());
//        ParametersWithIV aesCBCParams = (ParametersWithIV) pGen.generateDerivedParameters(256, 128);
//
//        aesCBC.init(false, aesCBCParams);
//        PaddedBufferedBlockCipher aesCipher = new PaddedBufferedBlockCipher(aesCBC,
//                new PKCS7Padding());
//        byte[] plainTemp = new byte[aesCipher.getOutputSize(cipher.length)];
//        int offset = aesCipher.processBytes(cipher, 0, cipher.length, plainTemp, 0);
//        int last = aesCipher.doFinal(plainTemp, offset);
//        final byte[] plain = new byte[offset + last];
//        System.arraycopy(plainTemp, 0, plain, 0, plain.length);
//        return plain;
//    }


