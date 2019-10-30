package it.richkmeli.jframework.crypto.util;

import it.richkmeli.jframework.crypto.algorithm.AES;
import it.richkmeli.jframework.crypto.algorithm.DiffieHellman;
import it.richkmeli.jframework.crypto.controller.payload.DiffieHellmanPayload;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class JSONHalper {

    public static String formatResponse(int state, String payload) {
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("state", state);
        responseJSON.put("payload", payload);
        return responseJSON.toString();
    }

    public static JSONObject pgToJSON(List<BigInteger> pg) {
        JSONObject pgJSON = new JSONObject();
        pgJSON.put("p", pg.get(0));
        pgJSON.put("g", pg.get(1));
        return pgJSON;
    }

    public static List<BigInteger> pgFromJSON(JSONObject pgJSON) {
        List<BigInteger> pg = new ArrayList<>();
        pg.add(pgJSON.getBigInteger("p"));
        pg.add(pgJSON.getBigInteger("g"));
        return pg;
    }

    public static JSONObject dhPrivateKeyToJSON(PrivateKey privateKey, List<BigInteger> pg) {
        JSONObject privateKeyJSON = new JSONObject();
        privateKeyJSON.put("enc", DiffieHellman.savePrivateKey(privateKey, pg.get(0), pg.get(1)));
        return privateKeyJSON;
    }

    public static JSONObject dhPublicKeyToJSON(PublicKey publicKey, List<BigInteger> pg) {
        JSONObject publicKeyJSON = new JSONObject();
        publicKeyJSON.put("enc", DiffieHellman.savePublicKey(publicKey, pg.get(0), pg.get(1)));
        return publicKeyJSON;
    }

    public static PrivateKey dhPrivateKeyFromJSON(JSONObject privateJSON) throws Exception {
        return DiffieHellman.loadPrivateKey(privateJSON.getString("enc"));
    }

    public static PublicKey dhPublicKeyFromJSON(JSONObject publicJSON) throws Exception {
        return DiffieHellman.loadPublicKey(publicJSON.getString("enc"));
    }

    public static JSONObject dhKeyPairToJSON(KeyPair keyPair, List<BigInteger> pg) {
        JSONObject keyPairJSON = new JSONObject();
        keyPairJSON.put("private", (dhPrivateKeyToJSON(keyPair.getPrivate(), pg)));
        keyPairJSON.put("public", (dhPublicKeyToJSON(keyPair.getPublic(), pg)));
        return keyPairJSON;
    }

    public static KeyPair dhKeyPairFromJSON(JSONObject keyPairJSON) throws Exception {
        PrivateKey privateKey = dhPrivateKeyFromJSON(keyPairJSON.getJSONObject("private"));
        PublicKey publicKey = dhPublicKeyFromJSON(keyPairJSON.getJSONObject("public"));
        return new KeyPair(publicKey, privateKey);
    }

    public static JSONObject dhPayloadToJSON(DiffieHellmanPayload diffieHellmanPayload) {
        JSONObject diffieHellmanPayloadJSON = new JSONObject();
        diffieHellmanPayloadJSON.put("pg", pgToJSON(diffieHellmanPayload.getPQ()));
        diffieHellmanPayloadJSON.put("public", dhPublicKeyToJSON(diffieHellmanPayload.getA(), diffieHellmanPayload.getPQ()));
        return diffieHellmanPayloadJSON;
    }


    public static DiffieHellmanPayload dhPayloadFromJSON(JSONObject diffieHellmanPayloadJSON) throws Exception {
        List<BigInteger> pg = pgFromJSON(diffieHellmanPayloadJSON.getJSONObject("pg"));
        PublicKey publicKey = dhPublicKeyFromJSON(diffieHellmanPayloadJSON.getJSONObject("public"));
        return new DiffieHellmanPayload(pg, publicKey);
    }

    public static JSONObject aesSecretKeyToJSON(SecretKey secretKey) {
        JSONObject secretKeyJSON = new JSONObject();
        secretKeyJSON.put("enc", AES.saveSecretKey(secretKey));
        return secretKeyJSON;
    }


    public static SecretKey aesSecretKeyFromJSON(JSONObject secretKeyJSON) {
        return AES.loadSecretKey(secretKeyJSON.getString("enc"));
    }
}
