package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.ec;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CipherParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECPoint;

public interface ECEncryptor {
    void init(CipherParameters params);

    ECPair encrypt(ECPoint point);
}
