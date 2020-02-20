package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.rainbow;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class RainbowKeyGenerationParameters
        extends KeyGenerationParameters {
    private RainbowParameters params;

    public RainbowKeyGenerationParameters(
            SecureRandom random,
            RainbowParameters params) {
        // TODO: key size?
        super(random, params.getVi()[params.getVi().length - 1] - params.getVi()[0]);
        this.params = params;
    }

    public RainbowParameters getParameters() {
        return params;
    }
}

