package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.algorithm.RC4;
import it.richkmeli.jframework.crypto.algorithm.SHA256;
import it.richkmeli.jframework.crypto.controller.CryptoControllerClient;
import it.richkmeli.jframework.crypto.controller.CryptoControllerServer;
import it.richkmeli.jframework.crypto.controller.PasswordManager;
import it.richkmeli.jframework.crypto.data.SecureDataManager;
import it.richkmeli.jframework.crypto.exception.CryptoException;

import java.io.File;

public class Crypto {

    public static class Client {
        //private String serverURL;
        private File secureData;
        private String secretKey;

        // Passive, payloads as parametes ant it return lib status
        public String init(File secureData, String secretKey, String serverPayload) {
            //this.serverURL = serverURL;
            this.secureData = secureData;
            this.secretKey = secretKey;

            return CryptoControllerClient.init(secureData, secretKey, serverPayload);
        }

        // Active, performs connections to server
       /* public boolean init(String serverURL, File secureData, String secretKey) {
            this.serverURL = serverURL;
            this.secureData = secureData;
            this.secretKey = secretKey;

            boolean init = CryptoControllerClient.init(serverURL, secureData, secretKey);
            Logger.info("CryptoControllerClient.init: " + init);
            return init;
        }*/

        /*public boolean initOffline(PublicKey publicKey_Server) {
            boolean init = CryptoControllerClient.initOffline(secureData, secretKey, publicKey_Server);
            Logger.info("CryptoControllerClient.init: " + init);
            return init;
        }*/

        public String encrypt(String message) throws CryptoException {
            return CryptoControllerClient.encrypt(message, secureData, secretKey);
        }

        public String decrypt(String chipertext) throws CryptoException {
            return CryptoControllerClient.decrypt(chipertext, secureData, secretKey);
        }

        public void reset() {
            CryptoControllerClient.reset(secureData, secretKey);
        }


//        // timeout: time in milliseconds
//        public String send(String message, int timeout) {
//            return CryptoControllerClient.send(message, timeout, secureData, secretKey);
//        }
//
//        public void asyncSend(String message, CryptoListener listener) {
//
//            // TODO il primo che passa il listener è chi implementa quella classe, poi viene passato fino al thread che quando finisce
//            // in modo async, va a chiamare listener.onResult, che sarà della classe di partenza, passandogli il valore error da li...
//            // probabilmente lo implementa il client (spiega in documentazione)
//            // o fare classe inner new CryptoListener(){...chiama funzione normale(magari stampa o aggiorna qualcosa nel db) della classe...}
//        }


    }

    public static class Server {
        private File secureData;
        private String secretKey;
        private String clientID;

        public String init(File secureData, String secretKey, String clientID, String clientPayload) {
            this.secureData = secureData;
            this.secretKey = secretKey;
            this.clientID = clientID;

            String init = CryptoControllerServer.init(secureData, secretKey, clientID, clientPayload);
            //Logger.info("CryptoControllerClient.init: " + init);
            return init;
        }


        /* public boolean init(File secureData, String clientID, String secretKey) {
            this.secureData = secureData;
            this.secretKey = secretKey;
            this.clientID = clientID;

            boolean init = CryptoControllerServer.init(secureData, secretKey, clientID);
            Logger.info("CryptoControllerClient.init: " + init);
            return init;
        }
*/
        public String encrypt(String payload) throws CryptoException {
            return CryptoControllerServer.encrypt(payload, secureData, secretKey, clientID);
        }

        public String decrypt(String payload) throws CryptoException {
            return CryptoControllerServer.decrypt(payload, secureData, secretKey, clientID);
        }

        public void deleteClientData() {
            CryptoControllerServer.deleteClientData(secureData, secretKey, clientID);
        }

    }


    public static String encryptRC4(String input, String key) {
        return RC4.encrypt(input, key);
    }

    public static String decryptRC4(String input, String key) throws CryptoException {
        return RC4.decrypt(input, key);
    }

    public static String hash(String input) {
        return SHA256.hash(input);
    }

    // salt is enabled only during login process, instead set it as false for saving passwords into DB
    public static String hashPassword(String password, boolean saltEnabled) {
        return PasswordManager.hashPassword(password, saltEnabled);
    }

    // hashedPassword = db password, hashedSaltPassword = login password
    public static boolean verifyPassword(String hashedPassword, String hashedSaltPassword) {
        return PasswordManager.verifyPassword(hashedPassword, hashedSaltPassword);
    }

    public static void putData(File file, String secretKey, String key, String value) {
        SecureDataManager.putData(file, secretKey, key, value);
    }

    public static String getData(File file, String secretKey, String key) {
        return SecureDataManager.getData(file, secretKey, key);
    }


}
