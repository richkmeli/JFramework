package it.richkmeli.jframework.crypto.data;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.exception.CryptoException;
import it.richkmeli.jframework.crypto.model.ClientSecureData;
import it.richkmeli.jframework.crypto.model.ServerSecureData;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class SecureDataManager {


    public static ClientSecureData getClientSecureData(File secureDataFile, String secretKey) {
        String decrypted = getSecureData(secureDataFile, secretKey);
        return new ClientSecureData(decrypted);
    }

    public static ServerSecureData getServerSecureData(File secureDataFile, String secretKey) {
        String decrypted = getSecureData(secureDataFile, secretKey);
        return new ServerSecureData(decrypted);
    }

    public static boolean setClientSecureData(File secureDataFile, String key, ClientSecureData clientSecureData) {
        String clientSecureDataJSON = clientSecureData.getClientSecureDataJSON();
        if (getClientSecureData(secureDataFile, key).getClientSecureDataJSON().equalsIgnoreCase(clientSecureDataJSON)) {
            //Logger.info("setClientSecureData, no changes, skipping");
            return true;
        } else {
            return setSecureData(secureDataFile, clientSecureDataJSON, key);
        }
    }

    // TODO prevedi salvataggio nel db embedded per evitare problemi di concorrenza
    public static boolean setServerSecureData(File secureDataFile, String key, ServerSecureData serverSecureData) {
        String serverSecureDataJSON = serverSecureData.getServerSecureDataJSON();

        if (getServerSecureData(secureDataFile, key).getServerSecureDataJSON().equalsIgnoreCase(serverSecureDataJSON)) {
            //Logger.info("setServerSecureData, no changes, skipping");
            return true;
        } else {
            return setSecureData(secureDataFile, serverSecureDataJSON, key);
        }
    }

    public static void initClientSecureData(File secureData, String secretKey) {
        ClientSecureData clientSecureData = new ClientSecureData(null, null, null, null);
        setClientSecureData(secureData, secretKey, clientSecureData);
    }

    public static void initServerSecureData(File secureData, String secretKey) {
        ServerSecureData serverSecureData = new ServerSecureData(null, null, null, null);
        setServerSecureData(secureData, secretKey, serverSecureData);
    }

    public static void putData(File file, String secretKey, String key, String value) {
        String data = SecureDataManager.getSecureData(file,secretKey);
        JSONObject jsonObject;
        if (data != null) {
            jsonObject = new JSONObject(data);
        }else {
            jsonObject = new JSONObject();
        }
        jsonObject.put(key,value);
        SecureDataManager.setSecureData(file,jsonObject.toString(),secretKey);
    }

    public static String getData(File file, String secretKey, String key) {
        String data = SecureDataManager.getSecureData(file,secretKey);
        JSONObject jsonObject;
        String value = "";
        if (data != null) {
            jsonObject = new JSONObject(data);
            if(jsonObject.has(key)){
                value = jsonObject.getString(key);
            }else {
                Logger.error("Crypto, getData: jsonObject doesn't contain key: " + key);
            }
        }else {
            Logger.error("Crypto, getData, file: "+file.getName()+" is empty");
        }
        return value;
    }

    private static String getSecureData(File secureDataFile, String secretKey) {
        String secureData = null;
        if (secureDataFile != null) {
            if (secureDataFile.exists()) {
                // reading content

                Scanner sc = null;
                try {
                    sc = new Scanner(secureDataFile);
                } catch (FileNotFoundException e) {
                    Logger.error("SecureData not found", e);
                    return null;
                }

                StringBuilder sb = new StringBuilder();
                while (sc.hasNextLine()) {
                    sb.append(sc.nextLine());
                }
                String encryptedSecureData = sb.toString();

                //Logger.info("SecureData read");

                if (encryptedSecureData.equalsIgnoreCase("")) {
                    Logger.error("Secure data is empty");
                } else {

                    String decrypted = "";
                    try {
                        decrypted = AES.decrypt(encryptedSecureData, secretKey);
                    } catch (CryptoException ce) {
                        Logger.error("Error decrypting SecureData", ce);
                        ce.printStackTrace();
                        return null;
                    }

                    secureData = decrypted;
                }
            } else {
                try {
                    if (secureDataFile.createNewFile()) {
                        Logger.info("SecureData created");
                    }
                } catch (IOException e) {
                    Logger.error("secureData creation", e);
                    return null;
                }
            }
        } else {
            Logger.error("secureDataFile is null");
            return null;
        }
        return secureData;
    }


    private static boolean setSecureData(File secureDataFile, String secureDataJSON, String key) {
        //Logger.info("secureDataJSON: " + secureDataJSON);

        String encrypted;
        try {
            encrypted = AES.encrypt(secureDataJSON, key);
        } catch (CryptoException ce) {
            Logger.error("Error encrypting SecureData", ce);
            return false;
        }

        if (secureDataFile.exists()) {
            Path path = Paths.get(secureDataFile.getPath());
            byte[] strToBytes = encrypted.getBytes();

            try {
                Files.write(path, strToBytes);
            } catch (IOException e) {
                Logger.error("Error writing SecureData to file", e);
                return false;
            }
            //Logger.info("SecureData set");
            return true;
        } else {
            try {
                if (secureDataFile.createNewFile()) {
                    Logger.info("SecureData created");
                }
            } catch (IOException e) {
                Logger.error("secureData creation", e);
                return false;
            }
        }

        return false;
    }


}
