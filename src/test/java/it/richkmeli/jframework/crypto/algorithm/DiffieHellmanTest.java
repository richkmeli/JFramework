package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.algorithm.bc.DiffieHellman_BC;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPair;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.agreement.DHAgreement;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.generators.DHKeyPairGenerator;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.*;
import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import it.richkmeli.jframework.crypto.data.model.ClientSecureData;
import it.richkmeli.jframework.crypto.data.model.ServerSecureData;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.List;

import static it.richkmeli.jframework.crypto.algorithm.algorithmTestUtil.genString;
import static it.richkmeli.jframework.crypto.algorithm.algorithmTestUtil.plainTextLengths;
import static org.junit.Assert.assertEquals;

public class DiffieHellmanTest {


    @Test
    public void diffieHellman() {
        try {
            // A and B are the parts of the communication example

            // ** A ------------------
            List<BigInteger> pg = DiffieHellman.dh0A_GeneratePrimeAndGenerator();
            KeyPair keys_A = DiffieHellman.dh1_GenerateKeyPair(pg);
            DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.dh2A_CreateDHPayload(pg, keys_A);
            // A sends --dhRequestPayload1-- to B

            // ** B ------------------
            KeyPair keys_B = DiffieHellman.dh1_GenerateKeyPair(diffieHellmanPayload.getPG());
            //DiffieHellmanPayload diffieHellmanPayload_B = DiffieHellman.dh2_CreateDHPayload(diffieHellmanPayload.getPG(), keys_B);
            PublicKey publicKey_B = keys_B.getPublic();
            // B sends --publicKey_B-- to A

            // **A ------------------
            String secretKey_A = DiffieHellman.dh3_CalculateSharedSecretKey(pg, publicKey_B, keys_A.getPrivate());
            String secretKey2_A = DiffieHellman.dh3_CalculateSharedSecretKey(pg, publicKey_B, keys_A.getPrivate());
            assertEquals(secretKey_A, secretKey2_A);

            // **B ------------------
            String secretKey_B = DiffieHellman.dh3_CalculateSharedSecretKey(diffieHellmanPayload.getPG(), diffieHellmanPayload.getPublicKey(), keys_B.getPrivate());

            System.out.println("DH_test, \nsecretKey_A: " + SHA256.hash(secretKey_A) + "  \nsecretKey_B: " + SHA256.hash(secretKey_B));
            assertEquals(secretKey_A, secretKey_B);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void diffieHellmanSaveAndLoadKeys() {
        try {
            List<BigInteger> pg = DiffieHellman.dh0A_GeneratePrimeAndGenerator();
            KeyPair keys_A = null;
            keys_A = DiffieHellman.dh1_GenerateKeyPair(pg);

            String priv = DiffieHellman.savePrivateKey(keys_A.getPrivate(), pg.get(0), pg.get(1));
            String pub = DiffieHellman.savePublicKey(keys_A.getPublic(), pg.get(0), pg.get(1));

            PrivateKey privateKey = DiffieHellman.loadPrivateKey(priv);
            PublicKey publicKey = DiffieHellman.loadPublicKey(pub);

            assertEquals(keys_A.getPublic(), publicKey);
            assertEquals(keys_A.getPrivate(), privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void diffieHellmanAES() {
        try {
            // A(client) and B(server) are the parts of the communication example

            // ** A
            List<BigInteger> pg = DiffieHellman.dh0A_GeneratePrimeAndGenerator();
            KeyPair keys_A = DiffieHellman.dh1_GenerateKeyPair(pg);
            DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.dh2A_CreateDHPayload(pg, keys_A);
            ClientSecureData clientSecureData = new ClientSecureData(keys_A, diffieHellmanPayload, null, null);
            // A sends --diffieHellmanPayload-- to B
            // ** B
            KeyPair keys_B = DiffieHellman.dh1_GenerateKeyPair(diffieHellmanPayload.getPG());
            PublicKey publicKey_B = keys_B.getPublic();
            ServerSecureData serverSecureData = new ServerSecureData(diffieHellmanPayload.getPG(), keys_B);
            serverSecureData.addDiffieHellmanPayload("ID", diffieHellmanPayload);
            // B sends --publicKey_B-- to A

            // **A
            clientSecureData.setPublicKeyServer(publicKey_B);

            SecretKey secretKey_A = DiffieHellman.dh3_CalculateSharedSecretKey(
                    clientSecureData.getDiffieHellmanPayload().getPG(),
                    clientSecureData.getPublicKeyServer(),
                    clientSecureData.getKeyPairClient().getPrivate(),
                    AES.ALGORITHM);
            clientSecureData.setSecretKey(secretKey_A);
            // **B
            SecretKey secretKey_B = DiffieHellman.dh3_CalculateSharedSecretKey(
                    serverSecureData.getDiffieHellmanPayload("ID").getPG(),
                    serverSecureData.getDiffieHellmanPayload("ID").getPublicKey(),
                    serverSecureData.getKeyPairServer().getPrivate(),
                    AES.ALGORITHM);
            serverSecureData.addSecretKey("ID", secretKey_B);

            //System.out.println("DH_test, secretKey_A: "+secretKey_A+"  secretKey_B: "+ secretKey_B);
            assertEquals(SHA256.hashToString(clientSecureData.getSecretKey().getEncoded()),
                    SHA256.hashToString(serverSecureData.getSecretKey("ID").getEncoded()));
            for (int i : plainTextLengths) {
                String plain = genString(i);
                String encrypted = AES.encrypt(plain, clientSecureData.getSecretKey());
                String decrypted = AES.decrypt(encrypted, serverSecureData.getSecretKey("ID"));
                assertEquals(plain, decrypted);
            }
            assertEquals(secretKey_A, secretKey_B);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void diffieHellmanGPWithRandom() {
        int bitLength = 512; // 512 bits
        SecureRandom rnd = new SecureRandom();
        BigInteger p = BigInteger.probablePrime(bitLength, rnd);
        BigInteger g = BigInteger.probablePrime(bitLength, rnd);

        p = p.nextProbablePrime();
        g = g.nextProbablePrime();

        DHParameters parameter = new DHParameters(p, g);
        rnd = new SecureRandom();
        DHKeyGenerationParameters dhKeyGenerationParameters = new DHKeyGenerationParameters(rnd, parameter);

        DHKeyPairGenerator keyPairGenerator = new DHKeyPairGenerator();
        keyPairGenerator.init(dhKeyGenerationParameters);

        //
        // generate first pair
        //
        AsymmetricCipherKeyPair pair = keyPairGenerator.generateKeyPair();

        DHPublicKeyParameters pu1 = (DHPublicKeyParameters) pair.getPublic();
        DHPrivateKeyParameters pv1 = (DHPrivateKeyParameters) pair.getPrivate();
        //
        // generate second pair
        //
        pair = keyPairGenerator.generateKeyPair();

        DHPublicKeyParameters pu2 = (DHPublicKeyParameters) pair.getPublic();
        DHPrivateKeyParameters pv2 = (DHPrivateKeyParameters) pair.getPrivate();

        //
        // two way
        //
        DHAgreement e1 = new DHAgreement();
        DHAgreement e2 = new DHAgreement();

        e1.init(new ParametersWithRandom(pv1, new SecureRandom()));
        e2.init(new ParametersWithRandom(pv2, new SecureRandom()));

        BigInteger m1 = e1.calculateMessage();
        BigInteger m1_2 = e1.calculateMessage(); // invalidate m1_2
        System.out.println("DH_test, \nMessage_A: " + m1 + "  \nMessage_A_2: " + m1_2);
        BigInteger m2 = e2.calculateMessage();
        System.out.println("DH_test, \nMessage_A: " + m1 + "  \nMessage_B: " + m2);


        BigInteger k1 = e1.calculateAgreement(pu2, m2);
        BigInteger k1_2 = e1.calculateAgreement(pu2, m2);
        assertEquals(k1, k1_2);
        BigInteger k2 = e2.calculateAgreement(pu1, m1_2);

        System.out.println("DH_test, \nsecretKey_A: " + k1 + "  \nsecretKey_B: " + k2);

        assert k1.equals(k2); //("basic with random 2-way test failed");
    }

    @Test
    public void diffieHellmanAES_BC_compatibility() {
        try {
            // A(client) and B(server) are the parts of the communication example

            // ** A
            List<BigInteger> pg = DiffieHellman.dh0A_GeneratePrimeAndGenerator();
            KeyPair keys_A = DiffieHellman.dh1_GenerateKeyPair(pg);
            DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.dh2A_CreateDHPayload(pg, keys_A);
            ClientSecureData clientSecureData = new ClientSecureData(keys_A, diffieHellmanPayload, null, null);
            // A sends --diffieHellmanPayload-- to B
            // ** B
            KeyPair keys_B = DiffieHellman_BC.dh1(diffieHellmanPayload.getPG());
            PublicKey publicKey_B = keys_B.getPublic();
            ServerSecureData serverSecureData = new ServerSecureData(diffieHellmanPayload.getPG(), keys_B);
            serverSecureData.addDiffieHellmanPayload("ID", diffieHellmanPayload);
            // B sends --publicKey_B-- to A

            // **A
            clientSecureData.setPublicKeyServer(publicKey_B);

            SecretKey secretKey_A = DiffieHellman.dh3_CalculateSharedSecretKey(
                    clientSecureData.getDiffieHellmanPayload().getPG(),
                    clientSecureData.getPublicKeyServer(),
                    clientSecureData.getKeyPairClient().getPrivate(),
                    AES.ALGORITHM);
//            SecretKey secretKey_A_BC = DiffieHellman_BC.dh3(
//                    clientSecureData.getKeyPairClient().getPrivate(),
//                    clientSecureData.getPublicKeyServer(),
//                    AES.ALGORITHM);
//            assertEquals(SHA256.hashToString(secretKey_A.getEncoded()),
//                    SHA256.hashToString(secretKey_A_BC.getEncoded()));
            clientSecureData.setSecretKey(secretKey_A);
            // **B
            SecretKey secretKey_B = DiffieHellman.dh3_CalculateSharedSecretKey(
                    serverSecureData.getDiffieHellmanPayload("ID").getPG(),
                    serverSecureData.getDiffieHellmanPayload("ID").getPublicKey(),
                    serverSecureData.getKeyPairServer().getPrivate(),
                    AES.ALGORITHM);
            serverSecureData.addSecretKey("ID", secretKey_B);

            //System.out.println("DH_test, secretKey_A: "+secretKey_A+"  secretKey_B: "+ secretKey_B);
            assertEquals(SHA256.hashToString(clientSecureData.getSecretKey().getEncoded()),
                    SHA256.hashToString(serverSecureData.getSecretKey("ID").getEncoded()));
            for (int i : plainTextLengths) {
                String plain = genString(i);
                String encrypted = AES.encrypt(plain, clientSecureData.getSecretKey());
                String decrypted = AES.decrypt(encrypted, serverSecureData.getSecretKey("ID"));
                assertEquals(plain, decrypted);
            }
            assertEquals(secretKey_A, secretKey_B);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

}