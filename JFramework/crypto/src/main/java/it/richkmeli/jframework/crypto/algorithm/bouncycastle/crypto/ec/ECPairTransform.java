package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.ec;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CipherParameters;

public interface ECPairTransform {
    void init(CipherParameters params);

    ECPair transform(ECPair cipherText);
}
