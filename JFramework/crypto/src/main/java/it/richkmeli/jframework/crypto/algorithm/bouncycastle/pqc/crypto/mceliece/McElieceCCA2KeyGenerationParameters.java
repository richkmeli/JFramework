package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.mceliece;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class McElieceCCA2KeyGenerationParameters
        extends KeyGenerationParameters {
    private McElieceCCA2Parameters params;

    public McElieceCCA2KeyGenerationParameters(
            SecureRandom random,
            McElieceCCA2Parameters params) {
        // XXX key size?
        super(random, 128);
        this.params = params;
    }

    public McElieceCCA2Parameters getParameters() {
        return params;
    }
}
