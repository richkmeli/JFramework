package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class Ed25519KeyGenerationParameters
        extends KeyGenerationParameters {
    public Ed25519KeyGenerationParameters(SecureRandom random) {
        super(random, 256);
    }
}
