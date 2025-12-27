package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.algorithm.AES;
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
        // private String serverURL;
        private File secureData;
        private String secretKey;

        // Passive, payloads as parametes ant it return lib status
        public String init(File secureData, String secretKey, String serverPayload) {
            // this.serverURL = serverURL;
            this.secureData = secureData;
            this.secretKey = secretKey;

            return CryptoControllerClient.init(secureData, secretKey, serverPayload);
        }

        // Active, performs connections to server
        /*
         * public boolean init(String serverURL, File secureData, String secretKey) {
         * this.serverURL = serverURL;
         * this.secureData = secureData;
         * this.secretKey = secretKey;
         * 
         * boolean init = CryptoControllerClient.init(serverURL, secureData, secretKey);
         * Logger.info("CryptoControllerClient.init: " + init);
         * return init;
         * }
         */

        /*
         * public boolean initOffline(PublicKey publicKey_Server) {
         * boolean init = CryptoControllerClient.initOffline(secureData, secretKey,
         * publicKey_Server);
         * Logger.info("CryptoControllerClient.init: " + init);
         * return init;
         * }
         */

        public String encrypt(String message) throws CryptoException {
            return CryptoControllerClient.encrypt(message, secureData, secretKey);
        }

        public String decrypt(String chipertext) throws CryptoException {
            return CryptoControllerClient.decrypt(chipertext, secureData, secretKey);
        }

        public void reset() {
            CryptoControllerClient.reset(secureData, secretKey);
        }

        // // timeout: time in milliseconds
        // public String send(String message, int timeout) {
        // return CryptoControllerClient.send(message, timeout, secureData, secretKey);
        // }
        //
        // public void asyncSend(String message, CryptoListener listener) {
        //
        // // TODO il primo che passa il listener è chi implementa quella classe, poi
        // viene passato fino al thread che quando finisce
        // // in modo async, va a chiamare listener.onResult, che sarà della classe di
        // partenza, passandogli il valore error da li...
        // // probabilmente lo implementa il client (spiega in documentazione)
        // // o fare classe inner new CryptoListener(){...chiama funzione normale(magari
        // stampa o aggiorna qualcosa nel db) della classe...}
        // }

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
            // Logger.info("CryptoControllerClient.init: " + init);
            return init;
        }

        /*
         * public boolean init(File secureData, String clientID, String secretKey) {
         * this.secureData = secureData;
         * this.secretKey = secretKey;
         * this.clientID = clientID;
         * 
         * boolean init = CryptoControllerServer.init(secureData, secretKey, clientID);
         * Logger.info("CryptoControllerClient.init: " + init);
         * return init;
         * }
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

    /**
     * Encrypts a string using RC4 algorithm.
     * 
     * @deprecated RC4 is considered insecure. Use
     *             {@link #encryptAES(String, String)} instead.
     *             This method will be removed in a future release.
     * @param input the plaintext to encrypt
     * @param key   the encryption key
     * @return the encrypted ciphertext (Base64Url encoded)
     * @see #encryptAES(String, String)
     */
    @Deprecated
    public static String encryptRC4(String input, String key) {
        return RC4.encrypt(input, key);
    }

    /**
     * Decrypts a string using RC4 algorithm.
     * 
     * @deprecated RC4 is considered insecure. Use
     *             {@link #decryptAES(String, String)} instead.
     *             This method will be removed in a future release.
     * @param input the ciphertext to decrypt (Base64Url encoded)
     * @param key   the decryption key
     * @return the decrypted plaintext
     * @throws CryptoException if decryption fails
     * @see #decryptAES(String, String)
     */
    @Deprecated
    public static String decryptRC4(String input, String key) throws CryptoException {
        return RC4.decrypt(input, key);
    }

    /**
     * Encrypts a string using AES-256-CBC algorithm (recommended).
     * 
     * <p>
     * This is the recommended encryption method for secure communication.
     * Uses AES-256-CBC with PKCS5 padding and SHA-256 key derivation.
     * A random 16-byte IV is generated for each encryption and prepended to the
     * ciphertext.
     * 
     * @param input the plaintext to encrypt
     * @param key   the encryption key (will be hashed with SHA-256 to derive
     *              256-bit key)
     * @return the encrypted ciphertext with IV (Base64Url encoded)
     * @throws CryptoException if encryption fails
     * @since 1.2.15
     */
    public static String encryptAES(String input, String key) throws CryptoException {
        return AES.encrypt(input, key);
    }

    /**
     * Decrypts a string using AES-256-CBC algorithm (recommended).
     * 
     * <p>
     * This is the recommended decryption method for secure communication.
     * Expects ciphertext encrypted with {@link #encryptAES(String, String)}.
     * The IV is automatically extracted from the beginning of the ciphertext.
     * 
     * @param input the ciphertext to decrypt (Base64Url encoded, with IV prepended)
     * @param key   the decryption key (will be hashed with SHA-256 to derive
     *              256-bit key)
     * @return the decrypted plaintext
     * @throws CryptoException if decryption fails (wrong key, tampered data, etc.)
     * @since 1.2.15
     */
    public static String decryptAES(String input, String key) throws CryptoException {
        return AES.decrypt(input, key);
    }

    public static String hash(String input) {
        return SHA256.hash(input);
    }

    // salt is enabled only during login process, instead set it as false for saving
    // passwords into DB
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
