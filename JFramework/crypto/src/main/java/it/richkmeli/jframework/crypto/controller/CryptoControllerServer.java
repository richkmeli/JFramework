package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.algorithm.DiffieHellman;
import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import it.richkmeli.jframework.crypto.data.SecureDataManager;
import it.richkmeli.jframework.crypto.data.SecureDataState;
import it.richkmeli.jframework.crypto.data.model.ServerSecureData;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.crypto.util.JSONHelper;
import it.richkmeli.jframework.util.log.Logger;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.io.File;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

public class CryptoControllerServer extends CryptoController {

    // Passive, payload as parameter
    public static String init(File secureData, String secretKey, String clientID, String clientPayload) {
        int state = checkState(secureData, secretKey, clientID);
        Logger.info("Server state: " + state);

        int stateS = 0;
        String payload = "";

        //Logger.info("clientPayload :" + clientPayload);
        switch (state) {
            case SecureDataState.NOT_INITIALIZED:
                SecureDataManager.initServerSecureData(secureData, secretKey);

                try {
                    // clientPayload = {"paylaod":"...","state":0}
                    //JSONObject clientPayloadJSON = new JSONObject(clientPayload);
                    //DiffieHellmanPayload diffieHellmanPayload = JSONmanager.DHPayloadFromJSON(new JSONObject(new String(Base64.getDecoder().decode(clientPayloadJSON.getString("paylaod")))));

                    // clientPayload = ... (content of payload)
                    String decodedPayload = new String(Base64.getUrlDecoder().decode(clientPayload));

                    // check if the other part is in a different state (not sync)
                    if (!decodedPayload.equalsIgnoreCase("")) {
                        DiffieHellmanPayload DiffieHellmanPayload = JSONHelper.dhPayloadFromJSON(new JSONObject(decodedPayload));

                        KeyPair keys_B = DiffieHellman.dh1_GenerateKeyPair(DiffieHellmanPayload.getPG());

                        PublicKey publicKey_B = keys_B.getPublic();
                        ServerSecureData serverSecureData = new ServerSecureData(DiffieHellmanPayload.getPG(), keys_B);
                        serverSecureData.addDiffieHellmanPayload(clientID, DiffieHellmanPayload);

                        SecureDataManager.setServerSecureData(secureData, secretKey, serverSecureData);

                        Logger.info("init, public keys exchanged");

                        serverSecureData = SecureDataManager.getServerSecureData(secureData, secretKey);

                        SecretKey secretKey_B = null;
                        secretKey_B = DiffieHellman.dh3_CalculateSharedSecretKey(
                                serverSecureData.getDiffieHellmanPayload(clientID).getPG(), serverSecureData.getDiffieHellmanPayload(clientID).getPublicKey(), serverSecureData.getKeyPairServer().getPrivate(),
                                AES.ALGORITHM);
                        serverSecureData.addSecretKey(clientID, secretKey_B);

                        SecureDataManager.setServerSecureData(secureData, secretKey, serverSecureData);
                        Logger.info("init, private key generated");

                        stateS = SecureDataState.SECRET_KEY_EXCHANGED;
                        payload = (JSONHelper.dhPublicKeyToJSON(publicKey_B, DiffieHellmanPayload.getPG())).toString();
                    } else {
                        Logger.error("init, server secure data has been removed or client is in a wrong state");

                        stateS = SecureDataState.ERROR;
                        payload = "server secure data has been removed or client is in a wrong state, reset!";
                    }
                } catch (Exception e) {
                    Logger.error("init, error generating private key", e);
                    e.printStackTrace();
                }
                break;
            case SecureDataState.SECRET_KEY_EXCHANGED:
                Logger.info("init, completed");
                stateS = SecureDataState.SECRET_KEY_EXCHANGED;
                payload = "";
                break;
            default:
                Logger.info("init, default case");
                break;
        }

        return JSONHelper.formatResponse(stateS, Base64.getUrlEncoder().encodeToString(payload.getBytes()));
    }

    // Active, servlet received data from client, not directly from a payload
    /*public static boolean init(File secureData, String secretKey, String clientID) {
        int state = checkState(secureData, secretKey, clientID);

        // Information in Securedata are set by specific servlets.

        switch (state) {
            case SecureDataState.NOT_INITIALIZED:
                SecureDataManager.initServerSecureData(secureData, secretKey);

                try {
                    // TODO servlet avrà ricevuto dal client info dati
                    // ora vengono gia settati nei secure data dal servizio mokkato

*//*                    KeyPair keys_B = DiffieHellman.DH_1(diffieHellmanPayload.getPQ());

                    PublicKey publicKey_B = keys_B.getPublic();
                    ServerSecureData serverSecureData = new ServerSecureData(keys_B);
                    serverSecureData.addDiffieHellmanPayload("ID", diffieHellmanPayload);
*//*

                    // SecureDataManager.setServerSecureData(secureData, secretKey, serverSecureData);
                    Logger.info("init, public keys exchanged");
                } catch (Exception e) {
                    Logger.error("init, error exchanging public keys", e);
                }
                break;
            case SecureDataState.PUBLIC_KEYS_EXCHANGED:
                ServerSecureData serverSecureData = SecureDataManager.getServerSecureData(secureData, secretKey);

                SecretKey secretKey_B = null;
                try {
                    secretKey_B = DiffieHellman.DH_3(serverSecureData.getKeyPairServer().getPrivate(),
                            serverSecureData.getDiffieHellmanPayload("ID").getA(),
                            AES.ALGORITHM);
                    serverSecureData.addSecretKey("ID", secretKey_B);

                    SecureDataManager.setServerSecureData(secureData, secretKey, serverSecureData);
                    Logger.info("init, private key generated");
                } catch (Exception e) {
                    Logger.error("init, error generating private key", e);
                }
                break;
            case SecureDataState.SECRET_KEY_EXCHANGED:
                Logger.info("init, completed");
                break;

        }

        return false;
    }*/


    private static int checkState(File secureDataFile, String secretKey, String clientID) {
        int state = SecureDataState.NOT_INITIALIZED;
        ServerSecureData serverSecureData = SecureDataManager.getServerSecureData(secureDataFile, secretKey);

        Map<String, DiffieHellmanPayload> map = serverSecureData.getDiffieHellmanPayloadMap();
        if (map != null) {
            if (map.containsKey(clientID)) {
                state = SecureDataState.PUBLIC_KEYS_EXCHANGED;
            } else {
                Logger.error("checkState, DiffieHellmanPayloadMap is not containing " + clientID);
            }
        } else {
            Logger.error("checkState, DiffieHellmanPayloadMap null");
        }

        Map<String, SecretKey> map2 = serverSecureData.getSecretKeyClientMap();
        if (map2 != null) {
            if (map2.containsKey(clientID)) {
                state = SecureDataState.SECRET_KEY_EXCHANGED;
            } else {
                Logger.error("checkState, SecretKey_ClientMap is not containing " + clientID);
            }
        } else {
            Logger.error("checkState, SecretKey_ClientMap null");
        }

        return state;
    }


    public static String encrypt(String message, File secureData, String secretKey, String clientID) throws CryptoException {
        int currentState = checkState(secureData, secretKey, clientID);
        if (currentState == SecureDataState.SECRET_KEY_EXCHANGED) {
            ServerSecureData serverSecureData = SecureDataManager.getServerSecureData(secureData, secretKey);
            String chipertext = null;
            try {
                chipertext = AES.encrypt(message, serverSecureData.getSecretKey(clientID));
            } catch (CryptoException e) {
                Logger.error("encrypt, Encryption error", e);
            }
            return chipertext;
        } else {
            Logger.error("encrypt, crypto not initialized, current state: " + currentState);
            throw new CryptoException("encrypt, crypto not initialized, current state: " + currentState);
        }
    }

    public static String decrypt(String message, File secureData, String secretKey, String clientID) throws CryptoException {
        int currentState = checkState(secureData, secretKey, clientID);
        if (currentState == SecureDataState.SECRET_KEY_EXCHANGED) {

            ServerSecureData serverSecureData = SecureDataManager.getServerSecureData(secureData, secretKey);
            String decrypted = null;
            try {
                decrypted = AES.decrypt(message, serverSecureData.getSecretKey(clientID));
            } catch (CryptoException e) {
                Logger.error("decrypt, Decryption error", e);
            }
            return decrypted;
        } else {
            Logger.error("decrypt, crypto not initialized, current state: " + currentState);
            throw new CryptoException("decrypt, crypto not initialized, current state: " + currentState);
        }
    }

    public static void deleteClientData(File secureData, String secretKey, String clientID) {
        // get from manager
        ServerSecureData serverSecureData = SecureDataManager.getServerSecureData(secureData, secretKey);
        System.out.println(serverSecureData.getServerSecureDataJSON());
        Map<String, DiffieHellmanPayload> map = serverSecureData.getDiffieHellmanPayloadMap();
        if (map != null) {
            if (map.containsKey(clientID)) {
                map.remove(clientID);
            } else {
                Logger.error("deleteClientData, DiffieHellmanPayloadMap is not containing " + clientID);
            }
        } else {
            Logger.error("deleteClientData, DiffieHellmanPayloadMap null");
        }
        // set data in serverSecureData variable
        serverSecureData.setDiffieHellmanPayloadMap(map);


        Map<String, SecretKey> map2 = serverSecureData.getSecretKeyClientMap();
        if (map2 != null) {
            if (map2.containsKey(clientID)) {
                map2.remove(clientID);
            } else {
                Logger.error("deleteClientData, SecretKey_ClientMap is not containing " + clientID);
            }
        } else {
            Logger.error("deleteClientData, SecretKey_ClientMap null");
        }
        // set data in serverSecureData variable
        serverSecureData.setSecretKeyClientMap(map2);

        // save to manager
        SecureDataManager.setServerSecureData(secureData, secretKey, serverSecureData);
        System.out.println(serverSecureData.getServerSecureDataJSON());
    }
}
