package it.richkmeli.jframework.crypto.system;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.system.FileManager;
import it.richkmeli.jframework.util.log.Logger;

import java.io.File;

public class SecureFileManager extends FileManager {

    public static String loadEncryptedDataFromFile(String path, String secretKey) {
        return loadEncryptedDataFromFile(new File(path), secretKey);
    }

    public static String loadEncryptedDataFromFile(File file, String secretKey) {
        String encryptedData = loadDataFromFile(file);

        if ("".equalsIgnoreCase(encryptedData)) {
            Logger.error("file '" + file.getName() + "' is empty");
            return encryptedData;
        } else {
            String decrypted = "";
            try {
                decrypted = AES.decrypt(encryptedData, secretKey);
            } catch (CryptoException ce) {
                Logger.error("Error decrypting file '" + file.getName() + "'", ce);
                //ce.printStackTrace();
                return null;
            }
            Logger.info("Decrypted: " + decrypted);
            return decrypted;
        }
    }

    public static boolean saveEncryptedDataToFile(String path, String input, String secretKey) {
        return saveEncryptedDataToFile(new File(path), input, secretKey);
    }

    public static boolean saveEncryptedDataToFile(File file, String input, String secretKey) {
        String encrypted = input;
        try {
            encrypted = AES.encrypt(input, secretKey);
        } catch (CryptoException ce) {
            Logger.error("Error encrypting file '" + file.getName() + "'", ce);
        }
        return saveDataToFile(file, encrypted);
    }
}
