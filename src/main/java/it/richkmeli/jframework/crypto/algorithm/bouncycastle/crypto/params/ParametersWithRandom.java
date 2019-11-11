package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CipherParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CryptoServicesRegistrar;

import java.security.SecureRandom;

public class ParametersWithRandom
        implements CipherParameters {
    private SecureRandom random;
    private CipherParameters parameters;

    public ParametersWithRandom(
            CipherParameters parameters,
            SecureRandom random) {
        this.random = random;
        this.parameters = parameters;
    }

    public ParametersWithRandom(
            CipherParameters parameters) {
        this(parameters, CryptoServicesRegistrar.getSecureRandom());
    }

    public SecureRandom getRandom() {
        return random;
    }

    public CipherParameters getParameters() {
        return parameters;
    }
}
