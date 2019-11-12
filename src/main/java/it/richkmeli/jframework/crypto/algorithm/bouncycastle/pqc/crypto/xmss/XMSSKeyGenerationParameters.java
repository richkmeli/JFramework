package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.xmss;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

/**
 * XMSS key-pair generation parameters.
 */
public final class XMSSKeyGenerationParameters
        extends KeyGenerationParameters {
    private final XMSSParameters xmssParameters;

    /**
     * XMSSMT constructor...
     *
     * @param prng Secure random to use.
     */
    public XMSSKeyGenerationParameters(XMSSParameters xmssParameters, SecureRandom prng) {
        super(prng, -1);

        this.xmssParameters = xmssParameters;
    }

    public XMSSParameters getParameters() {
        return xmssParameters;
    }
}
