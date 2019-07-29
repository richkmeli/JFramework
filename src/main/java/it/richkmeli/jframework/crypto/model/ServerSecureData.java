package it.richkmeli.jframework.crypto.model;

import it.richkmeli.jframework.crypto.util.JSONmanager;
import it.richkmeli.jframework.util.Logger;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// data that has to be saved in the system
public class ServerSecureData {
    private List<BigInteger> pg;
    private KeyPair keyPairServer;
    // ID,DiffieHellmanPayload - the public key of every client is in the DiffieHellmanPayload
    private Map<String, DiffieHellmanPayload> diffieHellmanPayloadMap;
    // ID,secretKey
    private Map<String, SecretKey> secretKeyClientMap;

    // constructor when maps are not present in storage
    public ServerSecureData(List<BigInteger> pg, KeyPair keyPairServer) {
        this.pg = pg;
        this.keyPairServer = keyPairServer;
        this.diffieHellmanPayloadMap = new HashMap<>();
        this.secretKeyClientMap = new HashMap<>();
    }

    // constructor when maps are already present in storage
    public ServerSecureData(List<BigInteger> pg, KeyPair keyPairServer, Map<String, DiffieHellmanPayload> diffieHellmanPayloadMap, Map<String, SecretKey> secretKeyClientMap) {
        this.pg = pg;
        this.keyPairServer = keyPairServer;
        this.diffieHellmanPayloadMap = diffieHellmanPayloadMap;
        this.secretKeyClientMap = secretKeyClientMap;
    }

    public String getServerSecureDataJSON() {
        JSONObject serverSecureDataJSON = new JSONObject();

        JSONObject pgJSON = (this.pg != null) ? JSONmanager.pgToJSON(this.pg) : new JSONObject();
        JSONObject keyPair_ServerJSON = (this.pg != null) ? JSONmanager.DHkeyPairToJSON(this.keyPairServer, this.pg) : new JSONObject();

        JSONObject diffieHellmanPayloadMapJSON = new JSONObject();
        if (this.diffieHellmanPayloadMap != null) {
            for (String s : this.diffieHellmanPayloadMap.keySet()) {
                DiffieHellmanPayload diffieHellmanPayload = this.diffieHellmanPayloadMap.get(s);
                diffieHellmanPayloadMapJSON.put(s, JSONmanager.DHPayloadToJSON(diffieHellmanPayload));
            }
        }

        JSONObject secretKey_ClientMapJSON = new JSONObject();
        if (this.secretKeyClientMap != null) {
            for (String s : this.secretKeyClientMap.keySet()) {
                SecretKey secretKey = this.secretKeyClientMap.get(s);
                secretKey_ClientMapJSON.put(s, JSONmanager.AESsecretKeyToJSON(secretKey));
            }
        }

        serverSecureDataJSON.put("pg", pgJSON);
        serverSecureDataJSON.put("keyPairServer", keyPair_ServerJSON);
        serverSecureDataJSON.put("diffieHellmanPayloadMap", diffieHellmanPayloadMapJSON);
        serverSecureDataJSON.put("secretKeyClientMap", secretKey_ClientMapJSON);

        return serverSecureDataJSON.toString();

    }


    public ServerSecureData(String serverSecureData) {
        try {

            if (serverSecureData != null) {
                if (!serverSecureData.equalsIgnoreCase("")) {
                    //Logger.info("serverSecureDataJSON: " + serverSecureData);

                    JSONObject serverSecureDataJSON = new JSONObject(serverSecureData);

                    JSONObject pgJSON = serverSecureDataJSON.has("pg") ? serverSecureDataJSON.getJSONObject("pg") : new JSONObject();
                    JSONObject keyPair_ServerJSON = serverSecureDataJSON.has("keyPairServer") ? serverSecureDataJSON.getJSONObject("keyPairServer") : new JSONObject();
                    JSONObject diffieHellmanPayloadMapJSON = serverSecureDataJSON.has("diffieHellmanPayloadMap") ? serverSecureDataJSON.getJSONObject("diffieHellmanPayloadMap") : new JSONObject();
                    JSONObject secretKey_ClientMapJSON = serverSecureDataJSON.has("secretKeyClientMap") ? serverSecureDataJSON.getJSONObject("secretKeyClientMap") : new JSONObject();

                    this.pg = (!pgJSON.toString().equalsIgnoreCase("{}")) ? JSONmanager.pgFromJSON(pgJSON) : null;
                    this.keyPairServer = (!keyPair_ServerJSON.toString().equalsIgnoreCase("{}")) ? JSONmanager.DHkeyPairFromJSON(keyPair_ServerJSON) : null;

                    Map<String, DiffieHellmanPayload> diffieHellmanPayloadMap = new HashMap<>();
                    for (String s : diffieHellmanPayloadMapJSON.keySet()) {
                        JSONObject diffieHellmanPayloadJSON = diffieHellmanPayloadMapJSON.getJSONObject(s);
                        diffieHellmanPayloadMap.put(s, JSONmanager.DHPayloadFromJSON(diffieHellmanPayloadJSON));
                    }

                    Map<String, SecretKey> secretKey_ClientMap = new HashMap<>();
                    for (String s : secretKey_ClientMapJSON.keySet()) {
                        JSONObject secretKey_ClientJSON = secretKey_ClientMapJSON.getJSONObject(s);
                        secretKey_ClientMap.put(s, JSONmanager.AESsecretKeyFromJSON(secretKey_ClientJSON));
                    }

                    this.diffieHellmanPayloadMap = diffieHellmanPayloadMap;
                    this.secretKeyClientMap = secretKey_ClientMap;

                } else {
                    Logger.error("ServerSecureData, JSON file is empty");
                }
            } else {
                Logger.error("ServerSecureData, JSON file is null");
            }
        } catch (Exception e) {
            Logger.error("ServerSecureData", e);
            e.printStackTrace();
        }
    }


    public KeyPair getKeyPairServer() {
        return keyPairServer;
    }

    public void setKeyPairServer(KeyPair keyPairServer) {
        this.keyPairServer = keyPairServer;
    }

    public Map<String, DiffieHellmanPayload> getDiffieHellmanPayloadMap() {
        return diffieHellmanPayloadMap;
    }

    public void setDiffieHellmanPayloadMap(Map<String, DiffieHellmanPayload> diffieHellmanPayloadMap) {
        this.diffieHellmanPayloadMap = diffieHellmanPayloadMap;
    }

    public Map<String, SecretKey> getSecretKeyClientMap() {
        return secretKeyClientMap;
    }

    public void setSecretKeyClientMap(Map<String, SecretKey> secretKeyClientMap) {
        this.secretKeyClientMap = secretKeyClientMap;
    }

    public void addDiffieHellmanPayload(String id, DiffieHellmanPayload diffieHellmanPayload) {
        this.diffieHellmanPayloadMap.put(id, diffieHellmanPayload);
    }

    public DiffieHellmanPayload getDiffieHellmanPayload(String id) {
        return this.diffieHellmanPayloadMap.get(id);
    }

    public void addSecretKey(String id, SecretKey secretKey) {
        this.secretKeyClientMap.put(id, secretKey);
    }

    public SecretKey getSecretKey(String id) {
        return this.secretKeyClientMap.get(id);
    }

    /*private static String secretKeyToBase64(SecretKey secretKey) {
        return (secretKey != null) ? (Base64.getEncoder().encodeToString(secretKey.getEncoded())) : "";
    }*/

}
   /*

    public String getServerSecureDataJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, ServerSecureData.class);
    }



public ServerSecureData(String serverSecureDataJSON) {

        if (serverSecureDataJSON != null) {
        if (!serverSecureDataJSON.equalsIgnoreCase("")) {
        Logger.info("serverSecureDataJSON: " + serverSecureDataJSON);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(PrivateKey.class, new ServerPrivateKeyInstanceCreator(serverSecureDataJSON));
                gsonBuilder.registerTypeAdapter(PublicKey.class, new ServerPublicKeyInstanceCreator(serverSecureDataJSON));
                gsonBuilder.registerTypeAdapter(SecretKey.class, new ServerSecretKeyInstanceCreator(serverSecureDataJSON));
                Gson gson = gsonBuilder.create();

                ServerSecureData serverSecureDataTMP = gson.fromJson(serverSecureDataJSON, ServerSecureData.class);


                if (serverSecureDataTMP != null) {
                    this.keyPairServer = serverSecureDataTMP.keyPairServer;
                    this.diffieHellmanPayloadMap = serverSecureDataTMP.diffieHellmanPayloadMap;
                    this.secretKeyClientMap = serverSecureDataTMP.secretKeyClientMap;
                } else {
                    Logger.error("ServerSecureData, Error parsing JSON");
                }
        } else {
        Logger.error("ServerSecureData, JSON file is empty");
        }
        } else {
        Logger.error("ServerSecureData, JSON file is null");
        }
        }

public static KeyPair generateKPfromSSDjson(String json) {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject keyPair_Server_jsonObject = jsonObject.getJSONObject("keyPairServer");
        JSONObject privateKey_jsonObject = keyPair_Server_jsonObject.getJSONObject("privateKey");
        String x = privateKey_jsonObject.getString("x");
        JSONObject publicKey_jsonObject = keyPair_Server_jsonObject.getJSONObject("publicKey");
        String y = publicKey_jsonObject.getString("y");
        List<BigInteger> pg = new ArrayList<>();
        pg.add(new BigInteger(x));
        pg.add(new BigInteger(y));
        KeyPair serverKeypair = null;
        try {
            DHPublicKeySpec dhPublicKeySpec = new DHPublicKeySpec(new BigInteger(y), pg.get(0), pg.get(1));
            KeyFactory kf = KeyFactory.getInstance("DH");
            PublicKey DHpub = kf.generatePublic(dhPublicKeySpec);
            DHPrivateKeySpec dhPrivateKeySpec = new DHPrivateKeySpec(new BigInteger(x), pg.get(0), pg.get(1));
            PrivateKey DHpriv = kf.generatePrivate(dhPrivateKeySpec);
            serverKeypair = new KeyPair(DHpub, DHpriv);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            Logger.error("Error creating server keypair from pg");
        }
        return serverKeypair;
    }
}

class ServerPrivateKeyInstanceCreator implements com.google.gson.InstanceCreator<PrivateKey> {
    private PrivateKey privateKey;

    public ServerPrivateKeyInstanceCreator(String json) {
        privateKey = ServerSecureData.generateKPfromSSDjson(json).getPrivate();
    }

    @Override
    public PrivateKey createInstance(Type type) {
        return privateKey;
    }
}

class ServerPublicKeyInstanceCreator implements com.google.gson.InstanceCreator<PublicKey> {
    private PublicKey publicKey;

    public ServerPublicKeyInstanceCreator(String json) {
        publicKey = ServerSecureData.generateKPfromSSDjson(json).getPublic();
    }

    @Override
    public PublicKey createInstance(Type type) {
        return publicKey;
    }
}

class ServerSecretKeyInstanceCreator implements com.google.gson.InstanceCreator<SecretKey> {
    private byte[] encoded;
    private String algorithm;

    public ServerSecretKeyInstanceCreator(String json) {
        JSONObject jsonObject = new JSONObject(json);
        System.out.println(json);
        JSONObject jsonObject1 = jsonObject.getJSONObject("secretKey");
        //value = jsonObject1.getJSONArray("key").toString();
        //value = jsonObject1.getString("key");
        //value = jsonObject.getString("secretKey");

        JSONArray value = jsonObject1.getJSONArray("key");
        encoded = new byte[value.length()];
        // parse every byte in JSONArray
        for (int i = 0; i < value.length(); i++) {
            encoded[i] = Byte.parseByte(Integer.toString((int) value.get(i)));
        }

        algorithm = jsonObject1.getString("algorithm");
    }

    @Override
    public SecretKey createInstance(Type type) {
        return new SecretKeySpec(encoded, algorithm);
    }
}


*/