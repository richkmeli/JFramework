package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

import java.security.SecureRandom;

public class Ed25519KeyPairGenerator
        implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;

    public void init(KeyGenerationParameters parameters) {
        this.random = parameters.getRandom();
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(random);
        Ed25519PublicKeyParameters publicKey = privateKey.generatePublicKey();
        return new AsymmetricCipherKeyPair(publicKey, privateKey);
    }
}
