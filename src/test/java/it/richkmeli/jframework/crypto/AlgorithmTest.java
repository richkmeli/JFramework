package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.algorithm.DiffieHellman;
import it.richkmeli.jframework.crypto.algorithm.RSA;
import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.crypto.model.ClientSecureData;
import it.richkmeli.jframework.crypto.model.DiffieHellmanPayload;
import it.richkmeli.jframework.crypto.model.ServerSecureData;
import it.richkmeli.jframework.crypto.util.RandomStringGenerator;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class AlgorithmTest {
    @Test
    public void RC4() {
        int[] plainTextLenghts = {8, 10, 100, 1000};
        int[] keyLenghts = {8, 10, 12};

        for (int i : plainTextLenghts) {
            for (int i2 : keyLenghts) {

                String plain = RandomStringGenerator.GenerateAlphanumericString(i);
                String key = RandomStringGenerator.GenerateAlphanumericString(i2);


                String encrypted = Crypto.encryptRC4(plain, key);

                String decrypted = Crypto.decryptRC4(encrypted, key);

                assertEquals(plain, decrypted);
            }
        }
    }

    @Test
    public void AES() {
        int[] plainTextLenghts = {8, 10, 100, 1000};

        for (int i : plainTextLenghts) {

            String plain = RandomStringGenerator.GenerateAlphanumericString(i);

            SecretKey AESsecretKey = null;
            try {
                AESsecretKey = AES.generateKey();
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                e.printStackTrace();
            }

            String decrypted = null;
            try {
                String encrypted = AES.encrypt(plain, AESsecretKey);
                decrypted = AES.decrypt(encrypted, AESsecretKey);
            } catch (CryptoException e) {
                e.printStackTrace();
            }

            assertEquals(plain, decrypted);
        }
    }

    @Test
    public void AES_String() {
        int[] plainTextLenghts = {8, 10, 100, 1000};
        String[] keyList = new String[]{"ciao", "crypto2", "1234597890p56789098", "12345978fdgfdgdfgfdgdf90p5678909845646464564"};

        for (String key : keyList) {
            for (int i : plainTextLenghts) {

                String plain = RandomStringGenerator.GenerateAlphanumericString(i);

                String decrypted = null;
                try {
                    String encrypted = AES.encrypt(plain, key);
                    decrypted = AES.decrypt(encrypted, key);
                } catch (CryptoException e) {
                    e.printStackTrace();
                }

                assertEquals(plain, decrypted);
            }
        }
    }

    @Test
    public void RSA_EncryptDecrypt() {
        try {
            KeyPair keyPair = RSA.generateKeyPair();

            PublicKey RSApublicKeyClient = keyPair.getPublic();
            PrivateKey RSAprivateKeyClient = keyPair.getPrivate();

            int[] plainTextLenghts = {8, 10, 100, 245};

            for (int i : plainTextLenghts) {

                byte[] plain = RandomStringGenerator.GenerateAlphanumericString(i).getBytes();

                byte[] decrypted = null;
                try {
                    byte[] encrypted = RSA.encrypt(plain, RSApublicKeyClient);

                    decrypted = RSA.decrypt(encrypted, RSAprivateKeyClient);

                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchProviderException e) {
                    e.printStackTrace();
                }

                assertEquals(Arrays.toString(plain), Arrays.toString(decrypted));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void diffieHellman() {
        try {
            // A and B are the parts of the communication example

            // ** A
            List<BigInteger> pg = DiffieHellman.DH_0_A();
            KeyPair keys_A = DiffieHellman.DH_1(pg);
            DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.DH_2_A(pg, keys_A.getPublic());
            // A sends --diffieHellmanPayload-- to B
            // ** B
            KeyPair keys_B = DiffieHellman.DH_1(diffieHellmanPayload.getPQ());
            PublicKey publicKey_B = keys_B.getPublic();
            // B sends --publicKey_B-- to A

            // **A
            String secretKey_A = DiffieHellman.DH_3(keys_A.getPrivate(), publicKey_B);
            // **B
            String secretKey_B = DiffieHellman.DH_3(keys_B.getPrivate(), diffieHellmanPayload.getA());

            //System.out.println("DH_test, secretKey_A: "+secretKey_A+"  secretKey_B: "+ secretKey_B);
            assertEquals(secretKey_A, secretKey_B);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void diffieHellmanKey() {
        try {
            List<BigInteger> pg = DiffieHellman.DH_0_A();
            KeyPair keys_A = null;
            keys_A = DiffieHellman.DH_1(pg);


            String priv = DiffieHellman.savePrivateKey(keys_A.getPrivate(), pg.get(0), pg.get(1));
            String pub = DiffieHellman.savePublicKey(keys_A.getPublic(), pg.get(0), pg.get(1));

            PrivateKey privateKey = DiffieHellman.loadPrivateKey(priv);
            PublicKey publicKey = DiffieHellman.loadPublicKey(pub);

            assertEquals(keys_A.getPublic(), publicKey);
            assertEquals(keys_A.getPrivate(), privateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void diffieHellmanAES() {
        try {
            // A(client) and B(server) are the parts of the communication example

            // ** A
            List<BigInteger> pg = DiffieHellman.DH_0_A();
            KeyPair keys_A = DiffieHellman.DH_1(pg);
            DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.DH_2_A(pg, keys_A.getPublic());
            ClientSecureData clientSecureData = new ClientSecureData(keys_A, diffieHellmanPayload, null, null);
            // A sends --diffieHellmanPayload-- to B
            // ** B
            KeyPair keys_B = DiffieHellman.DH_1(diffieHellmanPayload.getPQ());
            PublicKey publicKey_B = keys_B.getPublic();
            ServerSecureData serverSecureData = new ServerSecureData(diffieHellmanPayload.getPQ(), keys_B);
            serverSecureData.addDiffieHellmanPayload("ID", diffieHellmanPayload);
            // B sends --publicKey_B-- to A

            // **A
            clientSecureData.setPublicKeyServer(publicKey_B);
            SecretKey secretKey_A = DiffieHellman.DH_3(clientSecureData.getKeyPairClient().getPrivate(),
                    clientSecureData.getPublicKeyServer(),
                    AES.ALGORITHM);
            clientSecureData.setSecretKey(secretKey_A);
            // **B
            SecretKey secretKey_B = DiffieHellman.DH_3(serverSecureData.getKeyPairServer().getPrivate(),
                    serverSecureData.getDiffieHellmanPayload("ID").getA(),
                    AES.ALGORITHM);
            serverSecureData.addSecretKey("ID", secretKey_B);

            //System.out.println("DH_test, secretKey_A: "+secretKey_A+"  secretKey_B: "+ secretKey_B);
            String plain = "ciao";
            String encrypted = AES.encrypt(plain, clientSecureData.getSecretKey());

            String decrypted = AES.decrypt(encrypted, serverSecureData.getSecretKey("ID"));

            assertEquals(plain, decrypted);
            assertEquals(secretKey_A, secretKey_B);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void RSA_Key() {
        try {
            KeyPair keyPair = RSA.generateKeyPair();

            String priv = RSA.savePrivateKey(keyPair.getPrivate());
            String pub = RSA.savePublicKey(keyPair.getPublic());

            PrivateKey privateKey = RSA.loadPrivateKey(priv);
            PublicKey publicKey = RSA.loadPublicKey(pub);

            assertEquals(keyPair.getPublic(), publicKey);
            assertEquals(keyPair.getPrivate(), privateKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void SHA() {
        try {
            String a = "dsvsfvsef";
            String hash = SHA256.hash(a);
            System.out.println(a + " : " + hash);

           /* assertEquals(keyPair.getPublic(), publicKey);
            assertEquals(keyPair.getPrivate(), privateKey);
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}