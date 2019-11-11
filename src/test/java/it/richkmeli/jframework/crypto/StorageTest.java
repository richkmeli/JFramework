package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.algorithm.DiffieHellman;
import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import it.richkmeli.jframework.crypto.data.SecureDataManager;
import it.richkmeli.jframework.crypto.data.model.ClientSecureData;
import it.richkmeli.jframework.crypto.data.model.ServerSecureData;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class StorageTest {
    @Test
    public void clientSecureDataJSON() {
        File csdFile = new File("clientSecureData.txt");
        String clientKey = "testkeyClient";

        System.out.println("clientSecureData delete: " + csdFile.delete());

        // init and load random data
        SecureDataManager.initClientSecureData(csdFile, clientKey);
        ClientSecureData clientSecureData = fillRandomlyClientSecureData();
        // SAVE TO FILE
        String clientSecureDataJSON = clientSecureData.getClientSecureDataJSON();
        SecureDataManager.setClientSecureData(csdFile, clientKey, clientSecureData);
        // LOAD FROM FILE
        ClientSecureData loadedClientSecureData = SecureDataManager.getClientSecureData(csdFile, clientKey);
        String loadedClientSecureDataJSON = loadedClientSecureData.getClientSecureDataJSON();

        assertEquals(clientSecureDataJSON, loadedClientSecureDataJSON);
    }

    @Test
    public void serverSecureDataJSON() {
        File sdsFile = new File("serverSecureData.txt");
        //String clientID = "USER-001";
        String serverKey = "testkeyServer";

        System.out.println("serverSecureData delete: " + sdsFile.delete());

        SecureDataManager.initServerSecureData(sdsFile, serverKey);

        ServerSecureData serverSecureData = fillRandomlyServerSecureData();
        String serverSecureDataJSON = serverSecureData.getServerSecureDataJSON();
        SecureDataManager.setServerSecureData(sdsFile, serverKey, serverSecureData);

        ServerSecureData serverSecureData2 = SecureDataManager.getServerSecureData(sdsFile, serverKey);
        String serverSecureData2JSON = serverSecureData2.getServerSecureDataJSON();

        assertEquals(serverSecureDataJSON, serverSecureData2JSON);
    }


    private ClientSecureData fillRandomlyClientSecureData() {
        try {
            List<BigInteger> pg = DiffieHellman.dh0A_GeneratePrimeAndGenerator();
            KeyPair keys_A = DiffieHellman.dh1_GenerateKeyPair(pg);
            DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.dh2A_CreateDHPayload(pg, keys_A);
            SecretKey secretKey_A = DiffieHellman.dh3_CalculateSharedSecretKey(pg, diffieHellmanPayload.getPublicKey(), keys_A.getPrivate(), AES.ALGORITHM);
            return new ClientSecureData(keys_A, diffieHellmanPayload, keys_A.getPublic(), secretKey_A);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            assert false;
        }
        return null;
    }

    private ServerSecureData fillRandomlyServerSecureData() {
        try {
            List<BigInteger> pg = DiffieHellman.dh0A_GeneratePrimeAndGenerator();
            KeyPair keys_A = DiffieHellman.dh1_GenerateKeyPair(pg);
            DiffieHellmanPayload DiffieHellmanPayload = DiffieHellman.dh2A_CreateDHPayload(pg, keys_A);

            KeyPair keys_B = DiffieHellman.dh1_GenerateKeyPair(DiffieHellmanPayload.getPG());

            PublicKey publicKey = keys_B.getPublic();
            ServerSecureData serverSecureData = new ServerSecureData(pg, keys_B);
            serverSecureData.addDiffieHellmanPayload("ID", DiffieHellmanPayload);

            SecretKey secretKey_B = DiffieHellman.dh3_CalculateSharedSecretKey(
                    serverSecureData.getDiffieHellmanPayload("ID").getPG(), serverSecureData.getDiffieHellmanPayload("ID").getPublicKey(), serverSecureData.getKeyPairServer().getPrivate(),
                    AES.ALGORITHM);
            serverSecureData.addSecretKey("ID", secretKey_B);

            return serverSecureData;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            assert false;
        }
        return null;
    }

}