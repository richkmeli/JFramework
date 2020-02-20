package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.ec;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CipherParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECPoint;

public interface ECDecryptor {
    void init(CipherParameters params);

    ECPoint decrypt(ECPair cipherText);
}
