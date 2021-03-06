package it.richkmeli.jframework.crypto.algorithm;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class RSA {
    private static final int KEYSIZE = 2048;
    public static final String ALGORITHM = "RSA";
    private static final String ENCRYPT_DECRYPT_ALGORITHM = "RSA";
    private static final String SIGN_VERIFY_ALGORITHM = "SHA256withRSA";

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        //TODO RSAKeyParameters
        KeyPairGenerator keyPairGenerator = null;
        keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, "BC");
        keyPairGenerator.initialize(KEYSIZE);

        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] encrypt(byte[] plaintext, PublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {

        Cipher cipher = Cipher.getInstance(ENCRYPT_DECRYPT_ALGORITHM, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static byte[] decrypt(byte[] ciphertext, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {

        Cipher cipher = Cipher.getInstance(ENCRYPT_DECRYPT_ALGORITHM, "BC");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    public static String sign(byte[] plainText, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {

        Signature privateSignature = Signature.getInstance(SIGN_VERIFY_ALGORITHM, "BC");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText);

        return Base64.getEncoder().encodeToString(privateSignature.sign());
    }

    public static boolean verify(byte[] plainText, String signature, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {

        Signature publicSignature = Signature.getInstance(SIGN_VERIFY_ALGORITHM, "BC");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText);

        return publicSignature.verify(Base64.getDecoder().decode(signature));
    }


    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        byte[] clear = Base64.getDecoder().decode(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance(ALGORITHM, "BC");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }


    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(stored);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance(ALGORITHM, "BC");
        return fact.generatePublic(spec);
    }

    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance(ALGORITHM, "BC");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = Base64.getEncoder().encodeToString(packed);

        Arrays.fill(packed, (byte) 0);
        return key64;
    }


    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance(ALGORITHM, "BC");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        return Base64.getEncoder().encodeToString(spec.getEncoded());
    }
}