package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.generators;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPair;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.X25519PublicKeyParameters;

import java.security.SecureRandom;

public class X25519KeyPairGenerator
        implements AsymmetricCipherKeyPairGenerator {
    private SecureRandom random;

    public void init(KeyGenerationParameters parameters) {
        this.random = parameters.getRandom();
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        X25519PrivateKeyParameters privateKey = new X25519PrivateKeyParameters(random);
        X25519PublicKeyParameters publicKey = privateKey.generatePublicKey();
        return new AsymmetricCipherKeyPair(publicKey, privateKey);
    }
}
