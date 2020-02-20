package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface KeyEncoder {
    byte[] getEncoded(AsymmetricKeyParameter keyParameter);
}
