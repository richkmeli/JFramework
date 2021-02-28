package it.richkmeli.jframework.crypto.algorithm;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.*;
import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import it.richkmeli.jframework.util.log.Logger;

import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffieHellman {
    public static final String ALGORITHM = "DH";
    //public static final String SIGN_ALGORITHM = "SHA1";
    //private static BigInteger g512 = new BigInteger("1234567890", 16);
    //private static BigInteger p512 = new BigInteger("1234567890", 16);

    public static List<BigInteger> dh0A_GeneratePrimeAndGenerator() {
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength, rnd);
        BigInteger g = BigInteger.probablePrime(bitLength, rnd);

        p = p.nextProbablePrime();
        g = g.nextProbablePrime();

        return new ArrayList<>(Arrays.asList(p, g));

    }

    // generate keys pair
    public static KeyPair dh1_GenerateKeyPair(List<BigInteger> pg) throws NoSuchAlgorithmException, InvalidKeySpecException {
        BigInteger p = pg.get(0);
        BigInteger g = pg.get(1);

        // DHParameterSpec dhParams = new DHParameterSpec(p, g);
        DHParameters parameter = new DHParameters(p, g);
        SecureRandom rnd = new SecureRandom();

        DHKeyGenerationParameters dhKeyGenerationParameters = new DHKeyGenerationParameters(rnd, parameter);

        DHKeyPairGenerator keyPairGenerator = new DHKeyPairGenerator();
        keyPairGenerator.init(dhKeyGenerationParameters);

        AsymmetricCipherKeyPair asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();

        BigInteger publicY = ((DHPublicKeyParameters) asymmetricCipherKeyPair.getPublic()).getY();
        BigInteger privateX = ((DHPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate()).getX();

        DHPublicKey dhPublicKey = (DHPublicKey) KeyFactory.getInstance(ALGORITHM).generatePublic(new DHPublicKeySpec(publicY, p, g));
        DHPrivateKey dhPrivateKey = (DHPrivateKey) KeyFactory.getInstance(ALGORITHM).generatePrivate(new DHPrivateKeySpec(privateX, p, g));

        KeyPair keyPair = new KeyPair(dhPublicKey, dhPrivateKey);

        return keyPair;
    }

    // generate payload that has to be send
    public static DiffieHellmanPayload dh2A_CreateDHPayload(List<BigInteger> pg, KeyPair keyPair) {
        // calculate initial message
        DHPrivateKey dhPrivateKey_A = (DHPrivateKey) keyPair.getPrivate();
        DHParameters dhParameters = new DHParameters(pg.get(0), pg.get(1));
        DHPrivateKeyParameters pv_A = new DHPrivateKeyParameters(dhPrivateKey_A.getX(), dhParameters);

        DHAgreement dhAgreement = new DHAgreement();
        dhAgreement.init(new ParametersWithRandom(pv_A, new SecureRandom()));

        //AsymmetricCipherKeyPair keyPair_message = dhAgreement.calculateMessage2();

        return new DiffieHellmanPayload(pg, keyPair.getPublic());
    }


    // Common phase
    public static SecretKey dh3_CalculateSharedSecretKey(List<BigInteger> pg, PublicKey publicKey, PrivateKey privateKey_A, String algorithm) {
        DHPublicKey dhPublicKey_B = (DHPublicKey) publicKey;
        DHPrivateKey dhPrivateKey_A = (DHPrivateKey) privateKey_A;

        DHParameters dhParameters = new DHParameters(pg.get(0), pg.get(1));
        DHPublicKeyParameters pu_B = new DHPublicKeyParameters(dhPublicKey_B.getY(), dhParameters);
        DHPrivateKeyParameters pv_A = new DHPrivateKeyParameters(dhPrivateKey_A.getX(), dhParameters);

        DHAgreement dhAgreement = new DHAgreement();
        dhAgreement.init(pv_A/*new ParametersWithRandom(pv_A, new SecureRandom())*/);

        //dhAgreement.calculateMessage();
        dhAgreement.setPrivateValue(pv_A);
        BigInteger m_B = ((DHPublicKey) publicKey).getY();
        //System.out.println("m: " + m_B);
        BigInteger k_A = dhAgreement.calculateAgreement(pu_B, m_B);

        //System.out.println("k: " + k_A+"\n");
        SecretKey secretKey = null;
        switch (algorithm) {
            case AES.ALGORITHM:
                secretKey = AES.generateKey(k_A.toByteArray(), 32);
                //secretKey = SHA256.hash(secretKey.getEncoded());
                break;
            default:
                Logger.error("dh3_CalculateSharedSecretKey error: algorithm: " + algorithm + " is not supported");
        }

        return secretKey;
    }


    // Common phase
    public static String dh3_CalculateSharedSecretKey(List<BigInteger> pg, PublicKey publicKey, PrivateKey privateKey_A) {
        return new String(SHA256.hash(dh3_CalculateSharedSecretKey(pg, publicKey, privateKey_A, AES.ALGORITHM).getEncoded()));
    }

    public static PublicKey loadPublicKey(BigInteger y, BigInteger p, BigInteger g) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //DHParameters parameter = new DHParameters(p, g);
        //DHPublicKeyParameters dhPublicKeyParameters = new DHPublicKeyParameters(y, parameter);
        //return KeyPairHelper.bcAsymmetricKeyParameterToJcePublicKey(dhPublicKeyParameters, ALGORITHM);
        DHPublicKey dhPublicKey = (DHPublicKey) KeyFactory.getInstance(ALGORITHM).generatePublic(new DHPublicKeySpec(y, p, g));
        return dhPublicKey;
    }

    public static PrivateKey loadPrivateKey(BigInteger x, BigInteger p, BigInteger g) throws InvalidKeySpecException, NoSuchAlgorithmException {
        //DHParameters parameter = new DHParameters(p, g);
        //DHPrivateKeyParameters dhPrivateKeyParameters = new DHPrivateKeyParameters(x, parameter);
        //return KeyPairHelper.bcAsymmetricKeyParameterToJcePrivateKey(dhPrivateKeyParameters, ALGORITHM);
        DHPrivateKey dhPrivateKey = (DHPrivateKey) KeyFactory.getInstance(ALGORITHM).generatePrivate(new DHPrivateKeySpec(x, p, g));
        return dhPrivateKey;
    }


    public static String savePrivateKey(PrivateKey key, BigInteger p, BigInteger g) {
        DHPrivateKey dhPrivateKey = (DHPrivateKey) key;
        return (dhPrivateKey.getX() + "##" + p + "##" + g);
    }

    public static PrivateKey loadPrivateKey(String data) throws Exception {

        String[] privKeyAndGenS = data.split("##");
        BigInteger x = new BigInteger(privKeyAndGenS[0]);
        BigInteger p = new BigInteger(privKeyAndGenS[1]);
        BigInteger g = new BigInteger(privKeyAndGenS[2]);

        return loadPrivateKey(x, p, g);
    }


    public static String savePublicKey(PublicKey key, BigInteger p, BigInteger g) {
        DHPublicKey dhPublicKey = (DHPublicKey) key;
        return (dhPublicKey.getY() + "##" + p + "##" + g);
    }

    public static PublicKey loadPublicKey(String data) throws Exception {

        String[] pubKeyAndGenS = data.split("##");
        BigInteger y = new BigInteger(pubKeyAndGenS[0]);
        BigInteger p = new BigInteger(pubKeyAndGenS[1]);
        BigInteger g = new BigInteger(pubKeyAndGenS[2]);

        return loadPublicKey(y, p, g);
    }

}

