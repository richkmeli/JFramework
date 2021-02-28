package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class Ed448KeyGenerationParameters
        extends KeyGenerationParameters {
    public Ed448KeyGenerationParameters(SecureRandom random) {
        super(random, 448);
    }
}
