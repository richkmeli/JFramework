package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPair;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyPairHelper {

    public static KeyPair bcAsymmetricCipherKeyPairToJceKeyPair(AsymmetricCipherKeyPair bcKeyPair, String algorithm) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        return new KeyPair(bcAsymmetricKeyParameterToJcePublicKey(bcKeyPair.getPublic(), algorithm), bcAsymmetricKeyParameterToJcePrivateKey(bcKeyPair.getPrivate(), algorithm));
    }

    public static PublicKey bcAsymmetricKeyParameterToJcePublicKey(AsymmetricKeyParameter pubKey, String algorithm) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] spkiEncoded = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(pubKey).getEncoded();
        X509EncodedKeySpec spkiKeySpec = new X509EncodedKeySpec(spkiEncoded);
        KeyFactory keyFac = KeyFactory.getInstance(algorithm);
        return keyFac.generatePublic(spkiKeySpec);
    }

    public static PrivateKey bcAsymmetricKeyParameterToJcePrivateKey(AsymmetricKeyParameter privKey, String algorithm) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] pkcs8Encoded = PrivateKeyInfoFactory.createPrivateKeyInfo(privKey).getEncoded();
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(pkcs8Encoded);
        KeyFactory keyFac = KeyFactory.getInstance(algorithm);
        return keyFac.generatePrivate(pkcs8KeySpec);
    }

}
