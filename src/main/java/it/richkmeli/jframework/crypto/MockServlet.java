package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.algorithm.DiffieHellman;
import it.richkmeli.jframework.crypto.data.SecureDataManager;
import it.richkmeli.jframework.crypto.model.DiffieHellmanPayload;
import it.richkmeli.jframework.crypto.model.ServerSecureData;

import java.io.File;
import java.security.KeyPair;
import java.security.PublicKey;

public class MockServlet {
    public static PublicKey exchangePublicKeysServlet(DiffieHellmanPayload diffieHellmanPayload) {
        PublicKey publicKey = null;
        try {
            KeyPair keys_B = DiffieHellman.DH_1(diffieHellmanPayload.getPQ());

            publicKey = keys_B.getPublic();
            ServerSecureData serverSecureData = new ServerSecureData(diffieHellmanPayload.getPQ(), keys_B);
            serverSecureData.addDiffieHellmanPayload("ID", diffieHellmanPayload);

            SecureDataManager.setServerSecureData(new File("secureDataServer.txt"), "testkeyServer", serverSecureData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return publicKey;
    }
}
