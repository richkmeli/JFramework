package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.KeyGenerationParameters;

import java.security.SecureRandom;

public class X25519KeyGenerationParameters
        extends KeyGenerationParameters {
    public X25519KeyGenerationParameters(SecureRandom random) {
        super(random, 255);
    }
}
