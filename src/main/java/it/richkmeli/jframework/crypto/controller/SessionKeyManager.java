package it.richkmeli.jframework.crypto.controller;

import it.richkmeli.jframework.crypto.algorithm.SHA256;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SessionKeyManager {
    private static SecretKey deriveSessionKey(SecretKey secretKey) {
        // TODO use a standard algorithm such as KDF
        byte[] hashedSecretKey = SHA256.hash(secretKey.getEncoded());
        SecretKey derived = new SecretKeySpec(hashedSecretKey, secretKey.getAlgorithm());
        return derived;
    }
}
