package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.algorithm.RC4;
import it.richkmeli.jframework.crypto.algorithm.RSA;
import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.exception.CryptoException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Deprecated
public class CryptoCompat {


    public static String encryptRC4(String input, String key) {
        return RC4.encrypt(input, key);
    }

    public static String decryptRC4(String input, String key) {
        return RC4.decrypt(input, key);
    }

    public static KeyPair getGeneratedKeyPairRSA() throws CryptoException {
        try {
            return RSA.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new CryptoException(e);
        }
    }

    public static List<Object> keyExchangeAESRSA(KeyPair keyPairServer, String kpubClient) throws CryptoException {

        // String to public key
        KeyFactory keyFactory = null;
        PublicKey pubKeyClient;
        SecretKey AESsecretKey;
        try {
            /*byte[] publicBytes = DatatypeConverter.parseBase64Binary(kpubClient);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            keyFactory = KeyFactory.getInstance("RSA");
            pubKeyClient = keyFactory.generatePublic(keySpec);*/

            pubKeyClient = loadPublicKey(kpubClient);

            AESsecretKey = AES.generateKey();
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        PublicKey RSApublicKeyServer = keyPairServer.getPublic();
        PrivateKey RSAprivateKeyServer = keyPairServer.getPrivate();

        String signatureAESsecretKey = null;
        byte[] encryptedAESsecretKey = null;
        try {
            // sign AES key
            signatureAESsecretKey = RSA.sign(AESsecretKey.getEncoded(), RSAprivateKeyServer);
            encryptedAESsecretKey = RSA.encrypt(AESsecretKey.getEncoded(), pubKeyClient);
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        List<Object> results = new ArrayList<Object>();

        String encryptedAESsecretKeyS = Base64.getEncoder().encodeToString(encryptedAESsecretKey);
        signatureAESsecretKey = Base64.getEncoder().encodeToString(signatureAESsecretKey.getBytes());
        String RSApublicKeyServerS = null;
        try {

            RSApublicKeyServerS = savePublicKey(RSApublicKeyServer);
        } catch (GeneralSecurityException e) {
            throw new CryptoException(e);
        }

        results.add(new KeyExchangePayloadCompat(encryptedAESsecretKeyS, signatureAESsecretKey, RSApublicKeyServerS, null));
        results.add(AESsecretKey);

        return results;
    }


    //MODEL CHANGED
    public static SecretKey getAESKeyFromKeyExchange(KeyExchangePayloadCompat keyExchangePayloadCompat, PrivateKey RSAprivateKeyClient) throws CryptoException {
        byte[] encryptedAESsecretKey = Base64.getDecoder().decode(keyExchangePayloadCompat.getEncryptedAESsecretKey());
        String signatureAESsecretKey = new String(Base64.getDecoder().decode(keyExchangePayloadCompat.getSignatureAESsecretKey()));

        PublicKey kpubServer = null;
        try {
            kpubServer = loadPublicKey(keyExchangePayloadCompat.getKpubServer());
        } catch (GeneralSecurityException e) {
            throw new CryptoException(e);
        }

        byte[] AESsecretKey = null;
        try {
            AESsecretKey = RSA.decrypt(encryptedAESsecretKey, RSAprivateKeyClient);
            if (!(RSA.verify(AESsecretKey, signatureAESsecretKey, kpubServer))) {
                throw new CryptoException(new Exception("Failed to verify signature of message received. GetAESKeyFromKeyExchange()"));
            }
        } catch (Exception e) {
            throw new CryptoException(e);
        }

        return new SecretKeySpec(AESsecretKey, 0, AESsecretKey.length, "AES");

    }

    public static String encryptAES(String input, String key) throws CryptoException {
        return AES.encrypt(input, key);
    }

    public static String decryptAES(String input, String key) throws CryptoException {
        return AES.decrypt(input, key);
    }


    public static PrivateKey loadPrivateKey(String key64) throws GeneralSecurityException {
        return RSA.loadPrivateKey(key64);
    }


    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException {
        return RSA.loadPublicKey(stored);
    }

    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        return RSA.savePrivateKey(priv);
    }


    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        return RSA.savePublicKey(publ);
    }

    public static String hashSHA256(String input) {
        return SHA256.hash(input);
    }

}
