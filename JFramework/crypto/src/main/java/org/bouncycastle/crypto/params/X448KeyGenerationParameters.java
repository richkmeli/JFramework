package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class X448KeyGenerationParameters
        extends KeyGenerationParameters {
    public X448KeyGenerationParameters(SecureRandom random) {
        super(random, 448);
    }
}
