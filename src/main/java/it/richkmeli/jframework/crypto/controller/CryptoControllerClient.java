package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.algorithm.DiffieHellman;
import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import it.richkmeli.jframework.crypto.data.SecureDataManager;
import it.richkmeli.jframework.crypto.data.SecureDataState;
import it.richkmeli.jframework.crypto.data.model.ClientSecureData;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.crypto.util.JSONHalper;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.Base64;
import java.util.List;

public class CryptoControllerClient extends CryptoController {

    // Passive, payload as parameter ant it return lib status
    public static String init(File secureData, String secretKey, String serverPayload) {
        int state = checkState(secureData, secretKey);
        Logger.info("Client state: " + state);

        int stateS = 0;
        String payload = "";

        //Logger.info("clientPayload :" + serverPayload);
        switch (state) {
            case SecureDataState.NOT_INITIALIZED:
                SecureDataManager.initClientSecureData(secureData, secretKey);
                try {
                    List<BigInteger> pg = DiffieHellman.dh0A();
                    KeyPair keys_A = DiffieHellman.dh1(pg);
                    DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.dh2A(pg, keys_A.getPublic());
                    ClientSecureData clientSecureData = new ClientSecureData(keys_A, diffieHellmanPayload, null, null);

                    SecureDataManager.setClientSecureData(secureData, secretKey, clientSecureData);

                    stateS = SecureDataState.PUBLIC_KEY_GENERATED;
                    payload = JSONHalper.dhPayloadToJSON(diffieHellmanPayload).toString();
                    Logger.info("init, public key generated");
                } catch (Exception e) {
                    Logger.error("init, error generating public key", e);
                }
                break;

            case SecureDataState.PUBLIC_KEY_GENERATED:
                try {
                    // serverPayload = {"paylaod":"...","state":0}
                    //JSONObject serverPayloadJSON = new JSONObject(serverPayload);
                    //JSONObject publicKey = new JSONObject(new String(Base64.getDecoder().decode(serverPayloadJSON.getString("paylaod"))));

                    // serverPayload = ... (content of payload)
                    String decodedPayload = new String(Base64.getUrlDecoder().decode(serverPayload));
                    // check if the other part is in a different state (not sync)
                    if (!decodedPayload.equalsIgnoreCase("")) {
                        JSONObject publicKey = decodedPayload.equalsIgnoreCase("") ? new JSONObject() : new JSONObject(decodedPayload);

                        ClientSecureData clientSecureData = SecureDataManager.getClientSecureData(secureData, secretKey);
                        clientSecureData.setPublicKeyServer(JSONHalper.dhPublicKeyFromJSON(publicKey));
                        SecureDataManager.setClientSecureData(secureData, secretKey, clientSecureData);

                        Logger.info("init, public keys exchanged");

                        clientSecureData = SecureDataManager.getClientSecureData(secureData, secretKey);
                        SecretKey secretKey_A = DiffieHellman.dh3(clientSecureData.getKeyPairClient().getPrivate(),
                                clientSecureData.getPublicKeyServer(),
                                AES.ALGORITHM);
                        clientSecureData.setSecretKey(secretKey_A);

                        SecureDataManager.setClientSecureData(secureData, secretKey, clientSecureData);
                        Logger.info("init, private key generated");

                        stateS = SecureDataState.SECRET_KEY_EXCHANGED;
                        payload = "";
                    } else {
                        Logger.error("init, client secure data has been removed or server was already associated with this client");

                        stateS = SecureDataState.ERROR;
                        payload = "client secure data has been removed or server was already associated with this client, reset!";
                    }
                } catch (Exception e) {
                    Logger.error("init, error generating private key", e);
                }
                break;
            case SecureDataState.SECRET_KEY_EXCHANGED:
                String decodedPayload = new String(Base64.getUrlDecoder().decode(serverPayload));
                // check if the other part is in a different state (not sync)
                if (decodedPayload.equalsIgnoreCase("")) {
                    Logger.info("init, completed");
                    stateS = SecureDataState.SECRET_KEY_EXCHANGED;
                    payload = "";
                } else {
                    Logger.error("init, server secure data has been removed. Server is trying to renegotiate keys");

                    stateS = SecureDataState.ERROR;
                    payload = "server secure data has been removed. Server is trying to renegotiate keys, reset!";
                }

                break;
            default:
                Logger.info("init, default case");
                break;
        }

        return JSONHalper.formatResponse(stateS, Base64.getUrlEncoder().encodeToString(payload.getBytes()));
    }


    // Active, performs connections to server
    /* public static boolean init(String serverURL, File secureData, String secretKey*//*, PublicKey publicKey*//*) {
        int state = checkState(secureData, secretKey);

        do {
            switch (state) {
                case SecureDataState.NOT_INITIALIZED:
                    SecureDataManager.initClientSecureData(secureData, secretKey);

                    try {
                        List<BigInteger> pg = DiffieHellman.DH_0_A();
                        KeyPair keys_A = DiffieHellman.DH_1(pg);
                        DiffieHellmanPayload diffieHellmanPayload = DiffieHellman.DH_2_A(pg, keys_A.getPublic());
                        ClientSecureData clientSecureData = new ClientSecureData(keys_A, diffieHellmanPayload, null, null);

                        //if (publicKey == null){
                        // TO_DO fai scambio pubbliche, chiama una determinata servlet, invia pubblica error ricevi la pubblica generata con DH
                        PublicKey publicKey = MockServlet.exchangePublicKeysServlet(diffieHellmanPayload);
                        clientSecureData.setPublicKeyServer(publicKey);
                            *//*}else {
                            clientSecureData.setPublicKeyServer(publicKey);
                        }*//*

                        SecureDataManager.setClientSecureData(secureData, secretKey, clientSecureData);
                        Logger.info("init, public keys exchanged");
                    } catch (Exception e) {
                        Logger.error("init, error exchanging public keys", e);
                    }
                    break;
                case SecureDataState.PUBLIC_KEYS_EXCHANGED:
                    try {
                        ClientSecureData clientSecureData = SecureDataManager.getClientSecureData(secureData, secretKey);
                        SecretKey secretKey_A = DiffieHellman.DH_3(clientSecureData.getKeyPairClient().getPrivate(),
                                clientSecureData.getPublicKeyServer(),
                                AES.ALGORITHM);
                        clientSecureData.setSecretKey(secretKey_A);

                        SecureDataManager.setClientSecureData(secureData, secretKey, clientSecureData);
                        Logger.info("init, private key generated");
                    } catch (Exception e) {
                        Logger.error("init, error generating private key", e);
                    }
                    break;
                case SecureDataState.SECRET_KEY_EXCHANGED:
                    Logger.info("init, completed");
                    break;

            }
            state = checkState(secureData, secretKey);
        } while (state == SecureDataState.SECRET_KEY_EXCHANGED);

        return false;
    }
*/

    protected static int checkState(File secureDataFile, String secretKey) {
        int state = SecureDataState.NOT_INITIALIZED;
        ClientSecureData clientSecureData = SecureDataManager.getClientSecureData(secureDataFile, secretKey);

        // check if it is present keypair, so public key
        if (/*clientSecureData.getPublicKeyServer() != null &&*/ clientSecureData.getKeyPairClient() != null) {
            state = SecureDataState.PUBLIC_KEY_GENERATED;//PUBLIC_KEYS_EXCHANGED;
        } else {
            Logger.error("checkState, getKeyPairClient null");
        }

        if (clientSecureData.getSecretKey() != null) {
            state = SecureDataState.SECRET_KEY_EXCHANGED;
        } else {
            Logger.error("checkState, getSecretKey null");
        }

        return state;
    }

    public static String send(String message, int timeout, File secureDataFile, String secretKey) throws CryptoException {
        String encryptedPayload = encrypt(message, secureDataFile, secretKey);

        // TODO send payload
        return "{...response...}";
    }

    public static String encrypt(String message, File secureData, String secretKey) throws CryptoException {
        int currentState = checkState(secureData, secretKey);
        if (currentState == SecureDataState.SECRET_KEY_EXCHANGED) {
            ClientSecureData clientSecureData = SecureDataManager.getClientSecureData(secureData, secretKey);
            String chipertext = null;
            try {
                chipertext = AES.encrypt(message, clientSecureData.getSecretKey());
            } catch (CryptoException e) {
                Logger.error("send, Encryption error", e);
            }

            return chipertext;
        } else {
            Logger.error("encrypt, crypto not initialized, current state: " + currentState);
            throw new CryptoException("encrypt, crypto not initialized, current state: " + currentState);
        }
    }

    public static String decrypt(String message, File secureData, String secretKey) throws CryptoException {
        int currentState = checkState(secureData, secretKey);
        if (currentState == SecureDataState.SECRET_KEY_EXCHANGED) {
            ClientSecureData clientSecureData = SecureDataManager.getClientSecureData(secureData, secretKey);
            String decrypted = null;
            try {
                decrypted = AES.decrypt(message, clientSecureData.getSecretKey());
            } catch (CryptoException e) {
                Logger.error("decrypt, Decryption error", e);
            }
            return decrypted;
        } else {
            Logger.error("decrypt, crypto not initialized, current state: " + currentState);
            throw new CryptoException("decrypt, crypto not initialized, current state: " + currentState);
        }
    }

    public static void reset(File secureData, String secretKey) {
        SecureDataManager.initClientSecureData(secureData, secretKey);
    }


    // TODO init offline che usa certificato precedentemente generato, non DH
    /*public static boolean initOffline(File secureData, String secretKey, PublicKey publicKey_server) {
        return init(null,secureData,secretKey,publicKey_server);
    }*/
}
