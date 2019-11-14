package it.richkmeli.jframework.crypto.algorithm.bc;

import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffieHellman_BC {
    public static final String ALGORITHM = "DH";
    public static final String SIGN_ALGORITHM = "SHA1";
    private static final Provider PROVIDER = new BouncyCastleProvider();
    //private static BigInteger g512 = new BigInteger("1234567890", 16);
    //private static BigInteger p512 = new BigInteger("1234567890", 16);

    public static List<BigInteger> dh0A() {
        ProviderManager.init(PROVIDER);

        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength, rnd);
        BigInteger g = BigInteger.probablePrime(bitLength, rnd);

        p = p.nextProbablePrime();
        g = g.nextProbablePrime();

        return new ArrayList<>(Arrays.asList(p, g));

    }

    // Common phase
    public static KeyPair dh1(List<BigInteger> list) throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        BigInteger p = list.get(0);
        BigInteger g = list.get(1);

        DHParameterSpec dhParams = new DHParameterSpec(p, g);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
        keyPairGenerator.initialize(dhParams, new SecureRandom());

        return keyPairGenerator.generateKeyPair();
    }


    public static DiffieHellmanPayload dh2A(List<BigInteger> pg, PublicKey publicKey) {
        return new DiffieHellmanPayload(pg, publicKey);
    }


    // Common phase
    public static String dh3(PrivateKey privateKey_A, PublicKey publicKey_B) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement aKeyAgree = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
        MessageDigest hash = MessageDigest.getInstance(SIGN_ALGORITHM, PROVIDER);

        aKeyAgree.init(privateKey_A);
        aKeyAgree.doPhase(publicKey_B, true);

        return new String(hash.digest(aKeyAgree.generateSecret()));
    }


    // Common phase
    public static SecretKey dh3(PrivateKey privateKey_A, PublicKey publicKey_B, String algorithm) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement aKeyAgree = KeyAgreement.getInstance(ALGORITHM, PROVIDER);

        aKeyAgree.init(privateKey_A);
        aKeyAgree.doPhase(publicKey_B, true);

        return aKeyAgree.generateSecret(algorithm);
    }


    public static PublicKey loadPublicKey(String y, String p, String g) throws Exception {
        ProviderManager.init(PROVIDER);

        DHPublicKeySpec pubKey = new DHPublicKeySpec(new BigInteger(y), new BigInteger(p), new BigInteger(g));
        //params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return kf.generatePublic(pubKey);
    }

    public static PrivateKey loadPrivateKey(String x, String p, String g) throws Exception {
        ProviderManager.init(PROVIDER);

        DHPrivateKeySpec pubKey = new DHPrivateKeySpec(new BigInteger(x), new BigInteger(p), new BigInteger(g));
        //params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return kf.generatePrivate(pubKey);
    }

    public static String savePrivateKey(PrivateKey key, BigInteger p, BigInteger g) {

        //return key.getEncoded();
        DHPrivateKey dhPrivateKey = (DHPrivateKey) key;
        return (dhPrivateKey.getX().toString() + "##" + p + "##" + g);
    }

    public static PrivateKey loadPrivateKey(String data) throws Exception {
        ProviderManager.init(PROVIDER);

        String[] privKeyAndGenS = data.split("##");
        BigInteger x = new BigInteger(privKeyAndGenS[0]);
        BigInteger p = new BigInteger(privKeyAndGenS[1]);
        BigInteger g = new BigInteger(privKeyAndGenS[2]);

        DHPrivateKeySpec prvkey = new DHPrivateKeySpec(x, p, g);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return kf.generatePrivate(prvkey);
    }


    public static String savePublicKey(PublicKey key, BigInteger p, BigInteger g) {

        DHPublicKey dhPublicKey = (DHPublicKey) key;
        return (dhPublicKey.getY().toString() + "##" + p + "##" + g);
    }

    public static PublicKey loadPublicKey(String data) throws Exception {
        ProviderManager.init(PROVIDER);

        String[] pubKeyAndGenS = data.split("##");
        BigInteger y = new BigInteger(pubKeyAndGenS[0]);
        BigInteger p = new BigInteger(pubKeyAndGenS[1]);
        BigInteger g = new BigInteger(pubKeyAndGenS[2]);

        DHPublicKeySpec pubKey = new DHPublicKeySpec(y, p, g);
        //params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
        return kf.generatePublic(pubKey);
    }


}

