package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.gmss;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class GMSSKeyGenerationParameters
        extends KeyGenerationParameters {

    private GMSSParameters params;

    public GMSSKeyGenerationParameters(
            SecureRandom random,
            GMSSParameters params) {
        // XXX key size?
        super(random, 1);
        this.params = params;
    }

    public GMSSParameters getParameters() {
        return params;
    }
}